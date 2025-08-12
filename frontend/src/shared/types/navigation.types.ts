export interface BreadcrumbItem {
  label: string;
  path?: string;
}

export interface NavigationState {
  from?: string;
  breadcrumbs?: BreadcrumbItem[];
}