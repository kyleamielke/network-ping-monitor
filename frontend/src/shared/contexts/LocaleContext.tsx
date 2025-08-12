import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { format as dateFnsFormat } from 'date-fns';
// import { formatInTimeZone } from 'date-fns-tz';
import { enUS, es, fr, de, ja, zhCN } from 'date-fns/locale';

type Language = 'en' | 'es' | 'fr' | 'de' | 'ja' | 'zh';
type DateFormat = 'MM/DD/YYYY' | 'DD/MM/YYYY' | 'YYYY-MM-DD';
type TimeFormat = '12h' | '24h';
type MacAddressFormat = 'colon' | 'dash' | 'dot' | 'cisco' | 'none' | 'two-groups' | 'three-groups';

interface LocaleSettings {
  language: Language;
  timezone: string;
  dateFormat: DateFormat;
  timeFormat: TimeFormat;
  macAddressFormat: MacAddressFormat;
}

interface LocaleContextType {
  locale: LocaleSettings;
  setLanguage: (language: Language) => void;
  setTimezone: (timezone: string) => void;
  setDateFormat: (format: DateFormat) => void;
  setTimeFormat: (format: TimeFormat) => void;
  setMacAddressFormat: (format: MacAddressFormat) => void;
  formatDate: (date: Date | string, includeTime?: boolean) => string;
  formatTime: (date: Date | string) => string;
  formatRelativeTime: (date: Date | string) => string;
  formatMacAddress: (macAddress: string | null | undefined) => string;
  t: (key: string, params?: Record<string, any>) => string;
}

const LocaleContext = createContext<LocaleContextType | undefined>(undefined);

export const useLocale = () => {
  const context = useContext(LocaleContext);
  if (!context) {
    throw new Error('useLocale must be used within a LocaleProvider');
  }
  return context;
};

const LOCALE_STORAGE_KEY = 'support-locale-settings';

// Date-fns locale mapping
const dateFnsLocales = {
  en: enUS,
  es: es,
  fr: fr,
  de: de,
  ja: ja,
  zh: zhCN,
};

// Translation strings - in a real app, these would be in separate files
const enTranslations = {
    // Navigation
    'nav.dashboard': 'Dashboard',
    'nav.devices': 'Devices',
    'nav.alerts': 'Alerts',
    'nav.reports': 'Reports',
    'nav.settings': 'Settings',
    
    // Common
    'common.loading': 'Loading...',
    'common.error': 'Error',
    'common.save': 'Save',
    'common.cancel': 'Cancel',
    'common.delete': 'Delete',
    'common.edit': 'Edit',
    'common.create': 'Create',
    'common.refresh': 'Refresh',
    'common.search': 'Search',
    'common.filter': 'Filter',
    'common.actions': 'Actions',
    'common.status': 'Status',
    'common.online': 'Online',
    'common.offline': 'Offline',
    'common.unknown': 'Unknown',
    
    // Dashboard
    'dashboard.title': 'Network Monitoring Dashboard',
    'dashboard.subtitle': 'Monitoring {{count}} devices',
    'dashboard.totalDevices': 'Total Devices',
    'dashboard.onlineDevices': 'Online',
    'dashboard.offlineDevices': 'Offline',
    'dashboard.alertingDevices': 'Alerting',
    'dashboard.avgResponseTime': 'Avg Response',
    
    // Devices
    'devices.title': 'Device Management',
    'devices.subtitle': '{{count}} devices registered',
    'devices.addDevice': 'Add Device',
    'devices.name': 'Name',
    'devices.ipAddress': 'IP Address',
    'devices.type': 'Type',
    'devices.lastSeen': 'Last Seen',
    'devices.responseTime': 'Response Time',
    'devices.monitoring': 'Monitoring',
    'devices.startMonitoring': 'Start Monitoring',
    'devices.stopMonitoring': 'Stop Monitoring',
    
    // Alerts
    'alerts.title': 'Alert Management',
    'alerts.subtitle': '{{count}} unresolved alerts',
    'alerts.allAlerts': 'All Alerts',
    'alerts.unresolved': 'Unresolved',
    'alerts.unacknowledged': 'Unacknowledged',
    'alerts.device': 'Device',
    'alerts.type': 'Type',
    'alerts.message': 'Message',
    'alerts.created': 'Created',
    'alerts.acknowledge': 'Acknowledge',
    'alerts.resolve': 'Resolve',
    
    // Navigation
    'navigation.skipToMain': 'Skip to main content',
    
    // Settings
    'settings.title': 'Settings',
    'settings.subtitle': 'Configure your preferences and accessibility options',
    'settings.appearance': 'Appearance',
    'settings.themeMode': 'Theme Mode',
    'settings.light': 'Light',
    'settings.dark': 'Dark',
    'settings.highContrast': 'High Contrast',
    'settings.textSize': 'Text Size',
    'settings.small': 'Small',
    'settings.medium': 'Medium',
    'settings.large': 'Large',
    'settings.extraLarge': 'Extra Large',
    'settings.accessibility': 'Accessibility',
    'settings.reduceMotion': 'Reduce Motion',
    'settings.locale': 'Locale & Regional Settings',
    'settings.language': 'Language',
    'settings.timezone': 'Time Zone',
    'settings.dateFormat': 'Date Format',
    'settings.timeFormat': 'Time Format',
    'settings.macAddressFormat': 'MAC Address Format',
    'settings.macFormat.colon': 'Colon (FF:FF:FF:FF:FF:FF)',
    'settings.macFormat.dash': 'Dash (FF-FF-FF-FF-FF-FF)',
    'settings.macFormat.dot': 'Dot (FF.FF.FF.FF.FF.FF)',
    'settings.macFormat.cisco': 'Cisco (FFFF.FFFF.FFFF)',
    'settings.macFormat.none': 'No separators (FFFFFFFFFFFF)',
    'settings.macFormat.twoGroups': 'Two groups (FFFFFF-FFFFFF)',
    'settings.macFormat.threeGroups': 'Three groups (FFFF:FFFF:FFFF)',
    'settings.reset': 'Reset All Settings to Defaults',
    
    // Time
    'time.justNow': 'Just now',
    'time.minutesAgo': '{{count}} minutes ago',
    'time.hoursAgo': '{{count}} hours ago',
    'time.daysAgo': '{{count}} days ago',
};

const translations: Record<Language, Record<string, string>> = {
  en: enTranslations,
  // Spanish translations
  es: {
    'nav.dashboard': 'Panel de Control',
    'nav.devices': 'Dispositivos',
    'nav.alerts': 'Alertas',
    'nav.reports': 'Informes',
    'nav.settings': 'Configuración',
    'common.loading': 'Cargando...',
    'common.error': 'Error',
    'common.save': 'Guardar',
    'common.cancel': 'Cancelar',
    'common.delete': 'Eliminar',
    'common.edit': 'Editar',
    'common.create': 'Crear',
    'common.refresh': 'Actualizar',
    'common.search': 'Buscar',
    'common.online': 'En línea',
    'common.offline': 'Fuera de línea',
    'dashboard.title': 'Panel de Monitoreo de Red',
    'dashboard.subtitle': 'Monitoreando {{count}} dispositivos',
    'dashboard.totalDevices': 'Total de Dispositivos',
    'dashboard.onlineDevices': 'En línea',
    'dashboard.offlineDevices': 'Fuera de línea',
    'devices.title': 'Gestión de Dispositivos',
    'devices.subtitle': '{{count}} dispositivos registrados',
    'devices.addDevice': 'Añadir Dispositivo',
    'settings.title': 'Configuración',
    'settings.appearance': 'Apariencia',
    'settings.dark': 'Oscuro',
    'settings.light': 'Claro',
  },
  // Placeholder for other languages
  fr: { ...enTranslations },
  de: { ...enTranslations },
  ja: { ...enTranslations },
  zh: { ...enTranslations },
};

interface LocaleProviderProps {
  children: ReactNode;
}

export const LocaleProvider: React.FC<LocaleProviderProps> = ({ children }) => {
  const [locale, setLocale] = useState<LocaleSettings>(() => {
    const stored = localStorage.getItem(LOCALE_STORAGE_KEY);
    if (stored) {
      try {
        return JSON.parse(stored);
      } catch {
        // Invalid JSON
      }
    }
    return {
      language: 'en',
      timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
      dateFormat: 'MM/DD/YYYY',
      timeFormat: '12h',
      macAddressFormat: 'colon',
    };
  });

  // Save to localStorage whenever locale changes
  useEffect(() => {
    localStorage.setItem(LOCALE_STORAGE_KEY, JSON.stringify(locale));
  }, [locale]);

  const setLanguage = (language: Language) => {
    setLocale(prev => ({ ...prev, language }));
  };

  const setTimezone = (timezone: string) => {
    setLocale(prev => ({ ...prev, timezone }));
  };

  const setDateFormat = (dateFormat: DateFormat) => {
    setLocale(prev => ({ ...prev, dateFormat }));
  };

  const setTimeFormat = (timeFormat: TimeFormat) => {
    setLocale(prev => ({ ...prev, timeFormat }));
  };

  const setMacAddressFormat = (macAddressFormat: MacAddressFormat) => {
    setLocale(prev => ({ ...prev, macAddressFormat }));
  };

  const formatDate = (date: Date | string, includeTime = false): string => {
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    
    // Format patterns based on user preference
    const datePatterns: Record<DateFormat, string> = {
      'MM/DD/YYYY': 'MM/dd/yyyy',
      'DD/MM/YYYY': 'dd/MM/yyyy',
      'YYYY-MM-DD': 'yyyy-MM-dd',
    };
    
    const timePattern = locale.timeFormat === '12h' ? 'h:mm:ss a' : 'HH:mm:ss';
    const pattern = includeTime 
      ? `${datePatterns[locale.dateFormat]} ${timePattern}`
      : datePatterns[locale.dateFormat];
    
    // TODO: Add timezone support with date-fns-tz
    return dateFnsFormat(dateObj, pattern, { 
      locale: dateFnsLocales[locale.language] 
    });
  };

  const formatTime = (date: Date | string): string => {
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    const pattern = locale.timeFormat === '12h' ? 'h:mm:ss a' : 'HH:mm:ss';
    
    // TODO: Add timezone support with date-fns-tz
    return dateFnsFormat(dateObj, pattern, { 
      locale: dateFnsLocales[locale.language] 
    });
  };

  const formatRelativeTime = (date: Date | string): string => {
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    const now = new Date();
    const diffInSeconds = Math.floor((now.getTime() - dateObj.getTime()) / 1000);
    
    if (diffInSeconds < 60) return t('time.justNow');
    if (diffInSeconds < 3600) return t('time.minutesAgo', { count: Math.floor(diffInSeconds / 60) });
    if (diffInSeconds < 86400) return t('time.hoursAgo', { count: Math.floor(diffInSeconds / 3600) });
    if (diffInSeconds < 604800) return t('time.daysAgo', { count: Math.floor(diffInSeconds / 86400) });
    
    // For older dates, show the formatted date in the user's timezone
    return formatDate(dateObj);
  };

  const formatMacAddress = (macAddress: string | null | undefined): string => {
    if (!macAddress) return 'N/A';
    
    // Remove all separators and convert to uppercase
    const clean = macAddress.trim().replace(/[:.\\-]/g, '').toUpperCase();
    
    // Ensure we have exactly 12 hex characters
    if (clean.length !== 12 || !/^[0-9A-F]{12}$/.test(clean)) {
      return macAddress; // Return original if invalid
    }
    
    // Format based on user preference
    switch (locale.macAddressFormat) {
      case 'colon':
        // ff:ff:ff:ff:ff:ff
        return clean.match(/.{2}/g)!.join(':');
        
      case 'dash':
        // ff-ff-ff-ff-ff-ff
        return clean.match(/.{2}/g)!.join('-');
        
      case 'dot':
        // ff.ff.ff.ff.ff.ff
        return clean.match(/.{2}/g)!.join('.');
        
      case 'cisco':
        // ffff.ffff.ffff
        return clean.match(/.{4}/g)!.join('.');
        
      case 'none':
        // ffffffffffff
        return clean;
        
      case 'two-groups':
        // ffffff-ffffff
        return clean.substring(0, 6) + '-' + clean.substring(6);
        
      case 'three-groups':
        // ffff:ffff:ffff
        return clean.match(/.{4}/g)!.join(':');
        
      default:
        return clean.match(/.{2}/g)!.join(':');
    }
  };

  const t = (key: string, params?: Record<string, any>): string => {
    let translation = translations[locale.language][key] || translations.en[key] || key;
    
    // Replace parameters like {{count}}
    if (params) {
      Object.entries(params).forEach(([param, value]) => {
        translation = translation.replace(`{{${param}}}`, String(value));
      });
    }
    
    return translation;
  };

  const contextValue: LocaleContextType = {
    locale,
    setLanguage,
    setTimezone,
    setDateFormat,
    setTimeFormat,
    setMacAddressFormat,
    formatDate,
    formatTime,
    formatRelativeTime,
    formatMacAddress,
    t,
  };

  return (
    <LocaleContext.Provider value={contextValue}>
      {children}
    </LocaleContext.Provider>
  );
};