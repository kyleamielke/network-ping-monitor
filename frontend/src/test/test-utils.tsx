import React, { ReactElement } from 'react';
import { render, RenderOptions } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { MockedProvider, MockedResponse } from '@apollo/client/testing';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';

const theme = createTheme();

interface AllTheProvidersProps {
  children: React.ReactNode;
  mocks?: MockedResponse[];
}

const AllTheProviders: React.FC<AllTheProvidersProps> = ({ children, mocks = [] }) => {
  return (
    <MockedProvider mocks={mocks} addTypename={false}>
      <ThemeProvider theme={theme}>
        <LocalizationProvider dateAdapter={AdapterDateFns}>
          <BrowserRouter>
            {children}
          </BrowserRouter>
        </LocalizationProvider>
      </ThemeProvider>
    </MockedProvider>
  );
};

const customRender = (
  ui: ReactElement,
  options?: Omit<RenderOptions, 'wrapper'> & { mocks?: MockedResponse[] }
) => {
  const { mocks, ...renderOptions } = options || {};
  
  return render(ui, {
    wrapper: ({ children }) => (
      <AllTheProviders mocks={mocks}>{children}</AllTheProviders>
    ),
    ...renderOptions,
  });
};

export * from '@testing-library/react';
export { customRender as render };