import { useState, useCallback } from 'react';

export const useDialog = <T = any>(initialData?: T) => {
  const [open, setOpen] = useState(false);
  const [data, setData] = useState<T | undefined>(initialData);

  const openDialog = useCallback((newData?: T) => {
    if (newData !== undefined) {
      setData(newData);
    }
    setOpen(true);
  }, []);

  const closeDialog = useCallback(() => {
    setOpen(false);
  }, []);

  const resetDialog = useCallback(() => {
    setOpen(false);
    setData(initialData);
  }, [initialData]);

  return {
    open,
    data,
    openDialog,
    closeDialog,
    resetDialog,
  };
};