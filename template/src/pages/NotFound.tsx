import { Link, useNavigate } from 'react-router-dom';
import PageLayout from '../components/PageLayout';
import Button from '../components/Button';
import styles from './NotFound.module.css';

export default function NotFound() {
  const navigate = useNavigate();

  const handleLoginClick = () => {
    navigate('/login');
  };

  return (
    <PageLayout
      backgroundColor="light"
      isLoggedIn={false}
      onLoginClick={handleLoginClick}
    >
      <div className={styles.container}>
        <p className={styles.errorCode} aria-hidden="true">404</p>
        <h1 className={styles.title}>Page Not Found</h1>
        <p className={styles.message}>
          Sorry, the page you're looking for doesn't exist or has been moved.
        </p>
        <Link to="/" className={styles.homeLink} aria-label="Go back to homepage">
          <Button variant="primary" size="lg">
            Go to Homepage
          </Button>
        </Link>
      </div>
    </PageLayout>
  );
}
