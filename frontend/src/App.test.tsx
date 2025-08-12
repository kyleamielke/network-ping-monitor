import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';
import App from './App';

describe('App Component Tests', () => {
  it('should render App component without crashing', () => {
    render(<App />);
    expect(document.body).toBeInTheDocument();
  });

  it('should be a function component', () => {
    expect(typeof App).toBe('function');
  });

  it('should have correct component name', () => {
    expect(App.name).toBe('App');
  });

  it('should render without throwing errors', () => {
    expect(() => render(<App />)).not.toThrow();
  });

  it('should create DOM elements when rendered', () => {
    const { container } = render(<App />);
    expect(container.firstChild).toBeTruthy();
  });

  it('should be the main application component', () => {
    const { container } = render(<App />);
    expect(container).toBeDefined();
    expect(container.children.length).toBeGreaterThan(0);
  });
});