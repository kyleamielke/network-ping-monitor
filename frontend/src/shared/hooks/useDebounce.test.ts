import { describe, it, expect } from 'vitest';
import { renderHook } from '@testing-library/react';
import { useDebounce } from './useDebounce';

describe('useDebounce Hook Tests', () => {
  it('should be a function', () => {
    expect(typeof useDebounce).toBe('function');
  });

  it('should have correct function name', () => {
    expect(useDebounce.name).toBe('useDebounce');
  });

  it('should return initial value immediately', () => {
    const { result } = renderHook(() => useDebounce('initial', 500));
    expect(result.current).toBe('initial');
  });

  it('should handle string values', () => {
    const { result } = renderHook(() => useDebounce('test string', 100));
    expect(result.current).toBe('test string');
  });

  it('should handle number values', () => {
    const { result } = renderHook(() => useDebounce(42, 100));
    expect(result.current).toBe(42);
  });

  it('should handle boolean values', () => {
    const { result } = renderHook(() => useDebounce(true, 100));
    expect(result.current).toBe(true);
  });

  it('should handle null values', () => {
    const { result } = renderHook(() => useDebounce(null, 100));
    expect(result.current).toBe(null);
  });

  it('should handle undefined values', () => {
    const { result } = renderHook(() => useDebounce(undefined, 100));
    expect(result.current).toBe(undefined);
  });

  it('should handle array values', () => {
    const testArray = [1, 2, 3];
    const { result } = renderHook(() => useDebounce(testArray, 100));
    expect(result.current).toEqual(testArray);
  });

  it('should handle object values', () => {
    const testObject = { key: 'value' };
    const { result } = renderHook(() => useDebounce(testObject, 100));
    expect(result.current).toEqual(testObject);
  });

  it('should handle zero delay', () => {
    const { result } = renderHook(() => useDebounce('test', 0));
    expect(result.current).toBe('test');
  });

  it('should handle large delay values', () => {
    const { result } = renderHook(() => useDebounce('test', 10000));
    expect(result.current).toBe('test');
  });

  it('should maintain type safety with generic parameter', () => {
    // Test with string type
    const { result: stringResult } = renderHook(() => useDebounce<string>('typed string', 100));
    expect(typeof stringResult.current).toBe('string');

    // Test with number type
    const { result: numberResult } = renderHook(() => useDebounce<number>(123, 100));
    expect(typeof numberResult.current).toBe('number');

    // Test with boolean type
    const { result: booleanResult } = renderHook(() => useDebounce<boolean>(false, 100));
    expect(typeof booleanResult.current).toBe('boolean');
  });
});