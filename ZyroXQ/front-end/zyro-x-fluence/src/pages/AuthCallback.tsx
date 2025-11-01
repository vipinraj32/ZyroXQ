import { useEffect } from 'react';
import { handleOAuthCallback } from '@/lib/auth';

const AuthCallback = () => {
  useEffect(() => {
    const result = handleOAuthCallback();
    
    if (result.success) {
      // Redirect to dashboard on success
      window.location.href = '/advertiser-dashboard';
    } else {
      // Redirect to home with error on failure
      console.error('Authentication failed:', result.error);
      window.location.href = '/?error=' + encodeURIComponent(result.error || 'Authentication failed');
    }
  }, []);

  return (
    <div className="min-h-screen flex items-center justify-center bg-background">
      <div className="text-center">
        <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
        <h2 className="text-xl font-semibold text-foreground mb-2">Completing authentication...</h2>
        <p className="text-muted-foreground">Please wait while we log you in.</p>
      </div>
    </div>
  );
};

export default AuthCallback;