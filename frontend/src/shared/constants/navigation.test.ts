import { describe, it, expect } from 'vitest';
import { NAVIGATION_ITEMS, DRAWER_WIDTH, NavigationItem } from './navigation';

describe('Navigation Constants Tests', () => {
  it('should have all expected navigation items', () => {
    expect(NAVIGATION_ITEMS).toHaveLength(4);
    
    const expectedPaths = ['/dashboard', '/devices', '/alerts', '/reports'];
    const actualPaths = NAVIGATION_ITEMS.map(item => item.path);
    
    expect(actualPaths).toEqual(expectedPaths);
  });

  it('should have correct navigation item labels', () => {
    const expectedLabels = ['Dashboard', 'Devices', 'Alerts', 'Reports'];
    const actualLabels = NAVIGATION_ITEMS.map(item => item.label);
    
    expect(actualLabels).toEqual(expectedLabels);
  });

  it('should have drawer width constant', () => {
    expect(DRAWER_WIDTH).toBe(240);
    expect(typeof DRAWER_WIDTH).toBe('number');
  });

  it('should have valid navigation item structure', () => {
    NAVIGATION_ITEMS.forEach((item: NavigationItem) => {
      expect(item).toHaveProperty('path');
      expect(item).toHaveProperty('label');
      expect(item).toHaveProperty('icon');
      
      expect(typeof item.path).toBe('string');
      expect(typeof item.label).toBe('string');
      expect(typeof item.icon).toBe('object');
    });
  });

  it('should have all paths start with forward slash', () => {
    NAVIGATION_ITEMS.forEach(item => {
      expect(item.path).toMatch(/^\/\w+$/);
    });
  });

  it('should have non-empty labels', () => {
    NAVIGATION_ITEMS.forEach(item => {
      expect(item.label.length).toBeGreaterThan(0);
      expect(item.label.trim()).toBe(item.label);
    });
  });

  it('should have unique paths', () => {
    const paths = NAVIGATION_ITEMS.map(item => item.path);
    const uniquePaths = [...new Set(paths)];
    
    expect(paths.length).toBe(uniquePaths.length);
  });

  it('should have unique labels', () => {
    const labels = NAVIGATION_ITEMS.map(item => item.label);
    const uniqueLabels = [...new Set(labels)];
    
    expect(labels.length).toBe(uniqueLabels.length);
  });

  it('should export NavigationItem interface properly', () => {
    // Test that the interface is properly defined by creating a valid object
    const MockIcon = () => null;
    const testItem: NavigationItem = {
      path: '/test',
      label: 'Test',
      icon: MockIcon
    };
    
    expect(testItem.path).toBe('/test');
    expect(testItem.label).toBe('Test');
    expect(typeof testItem.icon).toBe('function');
  });

  it('should have correct dashboard item', () => {
    const dashboardItem = NAVIGATION_ITEMS.find(item => item.path === '/dashboard');
    
    expect(dashboardItem).toBeDefined();
    expect(dashboardItem?.label).toBe('Dashboard');
    expect(typeof dashboardItem?.icon).toBe('object');
  });
});