// Auth utility functions for OAuth2 providers
export interface AuthProvider {
  name: string;
  endpoint: string;
  scope?: string;
}

export const AUTH_PROVIDERS: Record<string, AuthProvider> = {
  google: {
    name: 'Google',
    endpoint: 'http://localhost:8082/api/auth/google',
    scope: 'profile email'
  },
  facebook: {
    name: 'Facebook',
    endpoint: 'http://localhost:8082/api/auth/facebook',
    scope: 'email public_profile'
  },
  instagram: {
    name: 'Instagram',
    endpoint: 'http://localhost:8082/api/auth/instagram',
    scope: 'user_profile user_media'
  }
};

export const initiateOAuthLogin = async (provider: keyof typeof AUTH_PROVIDERS) => {
  const authProvider = AUTH_PROVIDERS[provider];
  if (!authProvider) {
    throw new Error(`Unknown provider: ${provider}`);
  }

  try {
    // For OAuth2 login, we need to call the public endpoint without auth headers
    const response = await fetch(`http://localhost:8082/api/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        // No Authorization header for login endpoint
      },
      body: JSON.stringify({
        provider: provider,
        action: 'oauth_init',
        returnUrl: window.location.origin + '/auth/callback'
      }),
      // Don't include credentials for login endpoint
      credentials: 'omit'
    });

    if (response.ok) {
      const data = await response.json();
      console.log('OAuth response:', data);
      
      // If backend returns authorization URL, redirect to it
      if (data.authUrl || data.redirectUrl) {
        window.location.href = data.authUrl || data.redirectUrl;
        return;
      }
      
      // If backend returns token directly, handle it
      if (data.token) {
        localStorage.setItem('authToken', data.token);
        if (data.user) {
          localStorage.setItem('user', JSON.stringify(data.user));
        }
        window.location.href = '/advertiser-dashboard';
        return;
      }

      // If no specific response, try direct redirect
      console.log('No authUrl or token in response, trying direct redirect');
      window.location.href = authProvider.endpoint;
      
    } else {
      console.error('OAuth API call failed:', response.status, response.statusText);
      // Fallback: Direct redirect to OAuth endpoint
      window.location.href = authProvider.endpoint;
    }
    
  } catch (error) {
    console.error(`Error during ${provider} OAuth:`, error);
    // Fallback: Direct redirect to OAuth endpoint
    window.location.href = authProvider.endpoint;
  }
};

// Handle OAuth callback (if needed)
export const handleOAuthCallback = () => {
  const urlParams = new URLSearchParams(window.location.search);
  const token = urlParams.get('token');
  const error = urlParams.get('error');

  if (error) {
    console.error('OAuth error:', error);
    return { success: false, error };
  }

  if (token) {
    localStorage.setItem('authToken', token);
    return { success: true, token };
  }

  return { success: false, error: 'No token received' };
};

// Check if user is authenticated
export const isAuthenticated = (): boolean => {
  return !!localStorage.getItem('authToken');
};

// Get stored user data
export const getCurrentUser = () => {
  const userStr = localStorage.getItem('user');
  return userStr ? JSON.parse(userStr) : null;
};

// Logout function
export const logout = () => {
  localStorage.removeItem('authToken');
  localStorage.removeItem('user');
  window.location.href = '/';
};