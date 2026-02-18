import { useSearchParams } from 'react-router-dom';
import PageLayout from '../components/PageLayout';
import RoleSelector from '../components/RoleSelector';
import styles from './Login.module.css';

export default function Login() {
  const [searchParams] = useSearchParams();
  
  // Get preselected role from URL query parameter
  const roleParam = searchParams.get('role');
  const preselectedRole = (roleParam === 'admin' || roleParam === 'investor' || roleParam === 'innovator') 
    ? roleParam 
    : null;

  const handleRoleSelect = (role: 'admin' | 'investor' | 'innovator') => {
    // Navigation is handled internally by RoleSelector component
    // This callback can be used for additional logic like state management
    console.log(`Role selected: ${role}`);
  };

  const handleLoginClick = () => {
    // Already on login page, no action needed
  };

  return (
    <PageLayout
      backgroundColor="light"
      isLoggedIn={false}
      onLoginClick={handleLoginClick}
    >
      <div className={styles.loginPage}>
        <div className={styles.loginContainer}>
          <h1 className={styles.pageTitle}>Welcome to INVESTI</h1>
          <p className={styles.pageSubtitle}>
            Select your role to access the platform
          </p>
          <RoleSelector 
            onRoleSelect={handleRoleSelect} 
            preselectedRole={preselectedRole}
          />
        </div>
      </div>
    </PageLayout>
  );
}
