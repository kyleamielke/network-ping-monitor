import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  DialogContentText,
  Button,
  Slide,
} from '@mui/material';
import { TransitionProps } from '@mui/material/transitions';

const Transition = React.forwardRef(function Transition(
  props: TransitionProps & {
    children: React.ReactElement<any, any>;
  },
  ref: React.Ref<unknown>,
) {
  return <Slide direction="up" ref={ref} {...props} />;
});

interface ConfirmDialogProps {
  open: boolean;
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  confirmColor?: 'primary' | 'secondary' | 'error' | 'warning' | 'success';
  onConfirm: () => void;
  onCancel: () => void;
}

export const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
  open,
  title,
  message,
  confirmText = 'Confirm',
  cancelText = 'Cancel',
  confirmColor = 'primary',
  onConfirm,
  onCancel,
}) => {
  return (
    <Dialog 
      open={open} 
      onClose={onCancel} 
      maxWidth="xs" 
      fullWidth
      TransitionComponent={Transition}
      transitionDuration={225}
    >
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        <DialogContentText>{message}</DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button 
          onClick={onCancel} 
          color="inherit"
          sx={{
            transition: 'all 0.3s ease',
            '&:hover': {
              transform: 'translateX(-2px)',
            }
          }}
        >
          {cancelText}
        </Button>
        <Button 
          onClick={onConfirm} 
          color={confirmColor} 
          variant="contained"
          sx={{
            transition: 'all 0.3s ease',
            '&:hover': {
              transform: 'translateY(-2px)',
              boxShadow: 4,
            },
            '&:active': {
              transform: 'translateY(0)',
            }
          }}
        >
          {confirmText}
        </Button>
      </DialogActions>
    </Dialog>
  );
};