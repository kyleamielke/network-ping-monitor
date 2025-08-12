import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  FormControl,
  FormLabel,
  RadioGroup,
  FormControlLabel,
  Radio,
  Select,
  MenuItem,
  Switch,
  Divider,
  Grid,
  Button,
  Grow,
} from '@mui/material';
import {
  Brightness4 as DarkModeIcon,
  Contrast as ContrastIcon,
  TextFields as TextSizeIcon,
  Language as LanguageIcon,
  AccessTime as TimeZoneIcon,
  Settings as SettingsIcon,
} from '@mui/icons-material';
import { PageLayout } from '@/shared/components/PageLayout';
import { useTheme } from '@/shared/contexts/ThemeContext';
import { useLocale } from '@/shared/contexts/LocaleContext';


export const SettingsPage: React.FC = () => {
  const { themeSettings, setThemeMode, setTextSize, setReducedMotion } = useTheme();
  const { locale, setTimezone, setDateFormat, setTimeFormat, setMacAddressFormat, t } = useLocale();

  return (
    <PageLayout
      title={t('settings.title')}
      subtitle={t('settings.subtitle')}
    >
      <Grid container spacing={3}>
        {/* Appearance Settings */}
        <Grid item xs={12} md={6}>
          <Grow in={true} timeout={600}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" mb={3}>
                  <DarkModeIcon sx={{ mr: 1 }} />
                  <Typography variant="h6">{t('settings.appearance')}</Typography>
                </Box>

                <FormControl component="fieldset" fullWidth sx={{ mb: 3 }}>
                  <FormLabel component="legend">{t('settings.themeMode')}</FormLabel>
                  <RadioGroup
                    value={themeSettings.mode}
                    onChange={(e) => setThemeMode(e.target.value as any)}
                  >
                    <FormControlLabel 
                      value="light" 
                      control={<Radio />} 
                      label={t('settings.light')} 
                    />
                    <FormControlLabel 
                      value="dark" 
                      control={<Radio />} 
                      label={t('settings.dark')} 
                    />
                    <FormControlLabel 
                      value="high-contrast" 
                      control={<Radio />} 
                      label={
                        <Box display="flex" alignItems="center">
                          {t('settings.highContrast')}
                          <ContrastIcon sx={{ ml: 1, fontSize: '1.2rem' }} />
                        </Box>
                      } 
                    />
                  </RadioGroup>
                </FormControl>

                <Divider sx={{ my: 2 }} />

                <FormControl fullWidth>
                  <FormLabel sx={{ mb: 1 }}>
                    <Box display="flex" alignItems="center">
                      <TextSizeIcon sx={{ mr: 1 }} />
                      {t('settings.textSize')}
                    </Box>
                  </FormLabel>
                  <Select
                    value={themeSettings.textSize}
                    onChange={(e) => setTextSize(e.target.value as any)}
                  >
                    <MenuItem value="small">{t('settings.small')}</MenuItem>
                    <MenuItem value="medium">{t('settings.medium')} (Default)</MenuItem>
                    <MenuItem value="large">{t('settings.large')}</MenuItem>
                    <MenuItem value="extra-large">{t('settings.extraLarge')}</MenuItem>
                  </Select>
                </FormControl>
              </CardContent>
            </Card>
          </Grow>
        </Grid>

        {/* Accessibility Settings */}
        <Grid item xs={12} md={6}>
          <Grow in={true} timeout={800}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" mb={3}>
                  <SettingsIcon sx={{ mr: 1 }} />
                  <Typography variant="h6">{t('settings.accessibility')}</Typography>
                </Box>

                <FormControlLabel
                  control={
                    <Switch
                      checked={themeSettings.reducedMotion}
                      onChange={(e) => setReducedMotion(e.target.checked)}
                    />
                  }
                  label={
                    <Box>
                      <Typography variant="body1">{t('settings.reduceMotion')}</Typography>
                      <Typography variant="body2" color="text.secondary">
                        Minimize animations and transitions
                      </Typography>
                    </Box>
                  }
                  sx={{ mb: 2 }}
                />

                <Divider sx={{ my: 2 }} />

                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                  <strong>Keyboard Shortcuts:</strong>
                </Typography>
                <Typography variant="body2" color="text.secondary" component="div">
                  <ul style={{ margin: 0, paddingLeft: 20 }}>
                    <li><kbd>Alt</kbd> + <kbd>D</kbd> - Toggle dark mode</li>
                    <li><kbd>Alt</kbd> + <kbd>S</kbd> - Open settings</li>
                    <li><kbd>Alt</kbd> + <kbd>/</kbd> - Focus search</li>
                    <li><kbd>Esc</kbd> - Close dialogs</li>
                  </ul>
                </Typography>
              </CardContent>
            </Card>
          </Grow>
        </Grid>

        {/* Locale Settings */}
        <Grid item xs={12}>
          <Grow in={true} timeout={1000}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" mb={3}>
                  <LanguageIcon sx={{ mr: 1 }} />
                  <Typography variant="h6">{t('settings.locale')}</Typography>
                </Box>

                <Grid container spacing={3}>
                  <Grid item xs={12} md={6}>
                    <FormControl fullWidth>
                      <FormLabel sx={{ mb: 1 }}>
                        <Box display="flex" alignItems="center">
                          <TimeZoneIcon sx={{ mr: 1 }} />
                          {t('settings.timezone')}
                        </Box>
                      </FormLabel>
                      <Select
                        value={locale.timezone}
                        onChange={(e) => setTimezone(e.target.value)}
                      >
                        <MenuItem value="America/New_York">Eastern Time (ET)</MenuItem>
                        <MenuItem value="America/Chicago">Central Time (CT)</MenuItem>
                        <MenuItem value="America/Denver">Mountain Time (MT)</MenuItem>
                        <MenuItem value="America/Los_Angeles">Pacific Time (PT)</MenuItem>
                        <MenuItem value="Europe/London">London (GMT)</MenuItem>
                        <MenuItem value="Europe/Paris">Paris (CET)</MenuItem>
                        <MenuItem value="Asia/Tokyo">Tokyo (JST)</MenuItem>
                        <MenuItem value="Australia/Sydney">Sydney (AEDT)</MenuItem>
                        <MenuItem value={Intl.DateTimeFormat().resolvedOptions().timeZone}>
                          System Default ({Intl.DateTimeFormat().resolvedOptions().timeZone})
                        </MenuItem>
                      </Select>
                    </FormControl>
                  </Grid>

                  <Grid item xs={12} md={6}>
                    <FormControl fullWidth>
                      <FormLabel sx={{ mb: 1 }}>{t('settings.dateFormat')}</FormLabel>
                      <Select
                        value={locale.dateFormat}
                        onChange={(e) => setDateFormat(e.target.value as any)}
                      >
                        <MenuItem value="MM/DD/YYYY">MM/DD/YYYY (US)</MenuItem>
                        <MenuItem value="DD/MM/YYYY">DD/MM/YYYY (UK/EU)</MenuItem>
                        <MenuItem value="YYYY-MM-DD">YYYY-MM-DD (ISO)</MenuItem>
                      </Select>
                    </FormControl>
                  </Grid>

                  <Grid item xs={12} md={6}>
                    <FormControl fullWidth>
                      <FormLabel sx={{ mb: 1 }}>{t('settings.timeFormat')}</FormLabel>
                      <Select
                        value={locale.timeFormat}
                        onChange={(e) => setTimeFormat(e.target.value as any)}
                      >
                        <MenuItem value="12h">12-hour (AM/PM)</MenuItem>
                        <MenuItem value="24h">24-hour</MenuItem>
                      </Select>
                    </FormControl>
                  </Grid>

                  <Grid item xs={12} md={6}>
                    <FormControl fullWidth>
                      <FormLabel sx={{ mb: 1 }}>{t('settings.macAddressFormat')}</FormLabel>
                      <Select
                        value={locale.macAddressFormat}
                        onChange={(e) => setMacAddressFormat(e.target.value as any)}
                      >
                        <MenuItem value="colon">{t('settings.macFormat.colon')}</MenuItem>
                        <MenuItem value="dash">{t('settings.macFormat.dash')}</MenuItem>
                        <MenuItem value="dot">{t('settings.macFormat.dot')}</MenuItem>
                        <MenuItem value="cisco">{t('settings.macFormat.cisco')}</MenuItem>
                        <MenuItem value="none">{t('settings.macFormat.none')}</MenuItem>
                        <MenuItem value="two-groups">{t('settings.macFormat.twoGroups')}</MenuItem>
                        <MenuItem value="three-groups">{t('settings.macFormat.threeGroups')}</MenuItem>
                      </Select>
                    </FormControl>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grow>
        </Grid>

        {/* Reset Settings */}
        <Grid item xs={12}>
          <Grow in={true} timeout={1200}>
            <Box textAlign="center">
              <Button
                variant="outlined"
                onClick={() => {
                  if (confirm('Are you sure you want to reset all settings to defaults?')) {
                    // Reset theme settings
                    setThemeMode('light');
                    setTextSize('medium');
                    setReducedMotion(false);
                    
                    // Reset locale settings
                    setTimezone(Intl.DateTimeFormat().resolvedOptions().timeZone);
                    setDateFormat('MM/DD/YYYY');
                    setTimeFormat('12h');
                    setMacAddressFormat('colon');
                  }
                }}
              >
                {t('settings.reset')}
              </Button>
            </Box>
          </Grow>
        </Grid>
      </Grid>
    </PageLayout>
  );
};