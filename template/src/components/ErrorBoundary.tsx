import { Component, type ReactNode, type ErrorInfo } from 'react';
import Button from './Button';
import styles from './ErrorBoundary.module.css';

interface ErrorBoundaryProps {
  children: ReactNode;
  fallback?: ReactNode;
}

interface ErrorBoundaryState {
  hasError: boolean;
  error?: Error;
}

export default class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    // Log error for debugging
    console.error('ErrorBoundary caught an error:', error, errorInfo);
  }

  handleReset = (): void => {
    this.setState({ hasError: false, error: undefined });
  };

  render(): ReactNode {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback;
      }

      return (
        <div className={styles.container} role="alert" aria-live="assertive">
          <span className={styles.icon} aria-hidden="true">⚠️</span>
          <h1 className={styles.title}>Something went wrong</h1>
          <p className={styles.message}>
            We're sorry, but something unexpected happened. Please try again.
          </p>
          <Button
            variant="danger"
            size="lg"
            onClick={this.handleReset}
            aria-label="Try again to reload the content"
          >
            Try Again
          </Button>
        </div>
      );
    }

    return this.props.children;
  }
}
