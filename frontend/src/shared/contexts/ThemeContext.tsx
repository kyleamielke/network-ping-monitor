import React, { createContext, useContext, useState, useEffect, ReactNode, useMemo } from 'react';
import { ThemeProvider as MuiThemeProvider, createTheme, Theme } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';

type ThemeMode = 'light' | 'dark' | 'high-contrast';
type TextSize = 'small' | 'medium' | 'large' | 'extra-large';

interface ThemeSettings {
  mode: ThemeMode;
  textSize: TextSize;
  reducedMotion: boolean;
  highContrastMode: boolean;
}

interface ThemeContextType {
  themeSettings: ThemeSettings;
  setThemeMode: (mode: ThemeMode) => void;
  setTextSize: (size: TextSize) => void;
  setReducedMotion: (reduced: boolean) => void;
  toggleThemeMode: () => void;
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};

const THEME_STORAGE_KEY = 'support-theme-settings';

// Text size multipliers
const TEXT_SIZE_MULTIPLIERS: Record<TextSize, number> = {
  'small': 0.875,
  'medium': 1,
  'large': 1.125,
  'extra-large': 1.25,
};

// Create MUI theme based on settings
const createAppTheme = (settings: ThemeSettings): Theme => {
  const { mode, textSize, highContrastMode } = settings;
  const textMultiplier = TEXT_SIZE_MULTIPLIERS[textSize];
  
  const baseTheme = createTheme({
    palette: {
      mode: mode === 'high-contrast' ? 'dark' : mode,
      ...(mode === 'high-contrast' && {
        primary: {
          main: '#FFFFFF',
        },
        secondary: {
          main: '#FFD700',
        },
        background: {
          default: '#000000',
          paper: '#1a1a1a',
        },
        text: {
          primary: '#FFFFFF',
          secondary: '#FFD700',
        },
        error: {
          main: '#FF6B6B',
        },
        warning: {
          main: '#FFD93D',
        },
        success: {
          main: '#6BCF7F',
        },
      }),
    },
    typography: {
      fontSize: 14 * textMultiplier,
      h1: { fontSize: `${2.5 * textMultiplier}rem` },
      h2: { fontSize: `${2 * textMultiplier}rem` },
      h3: { fontSize: `${1.75 * textMultiplier}rem` },
      h4: { fontSize: `${1.5 * textMultiplier}rem` },
      h5: { fontSize: `${1.25 * textMultiplier}rem` },
      h6: { fontSize: `${1.1 * textMultiplier}rem` },
      body1: { fontSize: `${1 * textMultiplier}rem` },
      body2: { fontSize: `${0.875 * textMultiplier}rem` },
    },
    components: {
      MuiButton: {
        styleOverrides: {
          root: {
            minHeight: 44 * textMultiplier, // WCAG minimum touch target
            textTransform: 'none',
          },
        },
      },
      MuiIconButton: {
        styleOverrides: {
          root: {
            padding: 12 * textMultiplier,
          },
        },
      },
      MuiChip: {
        styleOverrides: {
          root: {
            ...(highContrastMode && {
              borderWidth: 2,
              borderStyle: 'solid',
            }),
          },
        },
      },
    },
  });

  // Apply reduced motion preferences
  if (settings.reducedMotion) {
    baseTheme.transitions.duration.shortest = 0;
    baseTheme.transitions.duration.shorter = 0;
    baseTheme.transitions.duration.short = 0;
    baseTheme.transitions.duration.standard = 0;
    baseTheme.transitions.duration.complex = 0;
    baseTheme.transitions.duration.enteringScreen = 0;
    baseTheme.transitions.duration.leavingScreen = 0;
  }

  return baseTheme;
};

interface ThemeProviderProps {
  children: ReactNode;
}

export const ThemeProvider: React.FC<ThemeProviderProps> = ({ children }) => {
  const [themeSettings, setThemeSettings] = useState<ThemeSettings>(() => {
    // Load from localStorage or use defaults
    const stored = localStorage.getItem(THEME_STORAGE_KEY);
    if (stored) {
      try {
        return JSON.parse(stored);
      } catch {
        // Invalid JSON, use defaults
      }
    }
    
    // Check system preferences
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    
    return {
      mode: prefersDark ? 'dark' : 'light',
      textSize: 'medium',
      reducedMotion: prefersReducedMotion,
      highContrastMode: false,
    };
  });

  // Save to localStorage whenever settings change
  useEffect(() => {
    localStorage.setItem(THEME_STORAGE_KEY, JSON.stringify(themeSettings));
  }, [themeSettings]);

  // Listen for system preference changes
  useEffect(() => {
    const darkModeQuery = window.matchMedia('(prefers-color-scheme: dark)');
    const motionQuery = window.matchMedia('(prefers-reduced-motion: reduce)');
    
    const handleDarkModeChange = (e: MediaQueryListEvent) => {
      if (themeSettings.mode !== 'high-contrast') {
        setThemeSettings(prev => ({
          ...prev,
          mode: e.matches ? 'dark' : 'light',
        }));
      }
    };
    
    const handleMotionChange = (e: MediaQueryListEvent) => {
      setThemeSettings(prev => ({
        ...prev,
        reducedMotion: e.matches,
      }));
    };
    
    darkModeQuery.addEventListener('change', handleDarkModeChange);
    motionQuery.addEventListener('change', handleMotionChange);
    
    return () => {
      darkModeQuery.removeEventListener('change', handleDarkModeChange);
      motionQuery.removeEventListener('change', handleMotionChange);
    };
  }, [themeSettings.mode]);

  const setThemeMode = (mode: ThemeMode) => {
    setThemeSettings(prev => ({
      ...prev,
      mode,
      highContrastMode: mode === 'high-contrast',
    }));
  };

  const setTextSize = (textSize: TextSize) => {
    setThemeSettings(prev => ({
      ...prev,
      textSize,
    }));
  };

  const setReducedMotion = (reducedMotion: boolean) => {
    setThemeSettings(prev => ({
      ...prev,
      reducedMotion,
    }));
  };

  const toggleThemeMode = () => {
    setThemeSettings(prev => {
      const modes: ThemeMode[] = ['light', 'dark'];
      const currentIndex = modes.indexOf(prev.mode === 'high-contrast' ? 'dark' : prev.mode);
      const nextIndex = (currentIndex + 1) % modes.length;
      return {
        ...prev,
        mode: modes[nextIndex],
        highContrastMode: false,
      };
    });
  };

  const theme = useMemo(() => createAppTheme(themeSettings), [themeSettings]);

  const contextValue: ThemeContextType = {
    themeSettings,
    setThemeMode,
    setTextSize,
    setReducedMotion,
    toggleThemeMode,
  };

  return (
    <ThemeContext.Provider value={contextValue}>
      <MuiThemeProvider theme={theme}>
        <CssBaseline />
        {children}
      </MuiThemeProvider>
    </ThemeContext.Provider>
  );
};