import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTheme } from '@/shared/contexts/ThemeContext';

export const useKeyboardShortcuts = () => {
  const navigate = useNavigate();
  const { toggleThemeMode } = useTheme();

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      // Alt + D: Toggle dark mode
      if (e.altKey && e.key === 'd') {
        e.preventDefault();
        toggleThemeMode();
      }
      
      // Alt + S: Open settings
      if (e.altKey && e.key === 's') {
        e.preventDefault();
        navigate('/settings');
      }
      
      // Alt + /: Focus search
      if (e.altKey && e.key === '/') {
        e.preventDefault();
        const searchInput = document.querySelector('[data-testid="global-search-input"]') as HTMLInputElement;
        if (searchInput) {
          searchInput.focus();
        }
      }
      
      // Escape: Close any open dialogs (handled by MUI Dialog component automatically)
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [navigate, toggleThemeMode]);
};