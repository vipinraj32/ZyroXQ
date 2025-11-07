import { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Instagram, Facebook, Loader2 } from "lucide-react";
import { initiateOAuthLogin } from "@/lib/auth";

interface LoginModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export const LoginModal = ({ open, onOpenChange }: LoginModalProps) => {
  const [isLoading, setIsLoading] = useState(false);
  const [loadingProvider, setLoadingProvider] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleOAuthLogin = async (provider: 'instagram' | 'facebook' | 'google') => {
    setIsLoading(true);
    setLoadingProvider(provider);
    setError(null);
    
    try {
      // Option 1: Try API call first
      const response = await fetch(`http://localhost:8082/api/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          provider: provider,
          action: 'oauth_start'
        }),
        credentials: 'omit' // Don't send cookies/auth for login
      });

      if (response.ok) {
        const data = await response.json();
        
        if (data.authUrl || data.redirectUrl) {
          // Backend provided OAuth URL
          window.location.href = data.authUrl || data.redirectUrl;
          return;
        }
        
        if (data.token) {
          // Direct token response
          localStorage.setItem('authToken', data.token);
          if (data.user) localStorage.setItem('user', JSON.stringify(data.user));
          onOpenChange(false);
          window.location.href = '/advertiser-dashboard';
          return;
        }
      }
      
      // Option 2: Direct redirect if API doesn't work
      console.log('API call failed or no redirect URL, using direct redirect');
      const directUrl = `http://localhost:8082/api/auth/${provider}?returnUrl=${encodeURIComponent(window.location.origin + '/auth/callback')}`;
      window.location.href = directUrl;
      
    } catch (error) {
      console.error(`Error during ${provider} login:`, error);
      
      // Fallback: Direct redirect
      const fallbackUrl = `http://localhost:8082/api/auth/${provider}`;
      window.location.href = fallbackUrl;
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md bg-background/95 backdrop-blur-xl border border-border/50 shadow-2xl">
        <DialogHeader className="space-y-3">
          <DialogTitle className="text-center text-2xl font-bold bg-gradient-to-r from-primary to-primary-glow bg-clip-text text-transparent">
            Welcome to ZyroXQ
          </DialogTitle>
          <p className="text-center text-muted-foreground">
            Choose your preferred way to sign in and start your influencer marketing journey
          </p>
        </DialogHeader>

        <div className="flex flex-col gap-3 mt-8">
          {error && (
            <div className="p-3 bg-red-500/10 border border-red-500/20 rounded-lg">
              <p className="text-sm text-red-400 text-center">{error}</p>
            </div>
          )}
          
          {/* Instagram Login */}
          <Button
            variant="outline"
            size="lg"
            className="w-full h-14 border-2 border-pink-500/20 hover:border-pink-500/40 hover:bg-pink-500/10 transition-all duration-300 group"
            onClick={() => handleOAuthLogin('instagram')}
            disabled={isLoading}
          >
            {loadingProvider === 'instagram' ? (
              <Loader2 className="w-5 h-5 mr-3 animate-spin" />
            ) : (
              <Instagram className="w-5 h-5 mr-3 text-pink-500 group-hover:scale-110 transition-transform" />
            )}
            <span className="text-foreground font-medium">Continue with Instagram</span>
          </Button>

          {/* Facebook Login */}
          <Button
            variant="outline"
            size="lg"
            className="w-full h-14 border-2 border-blue-500/20 hover:border-blue-500/40 hover:bg-blue-500/10 transition-all duration-300 group"
            onClick={() => handleOAuthLogin('facebook')}
            disabled={isLoading}
          >
            {loadingProvider === 'facebook' ? (
              <Loader2 className="w-5 h-5 mr-3 animate-spin" />
            ) : (
              <Facebook className="w-5 h-5 mr-3 text-blue-500 group-hover:scale-110 transition-transform" />
            )}
            <span className="text-foreground font-medium">Continue with Facebook</span>
          </Button>

          {/* Google Login */}
          <Button
            variant="outline"
            size="lg"
            className="w-full h-14 border-2 border-red-500/20 hover:border-red-500/40 hover:bg-red-500/10 transition-all duration-300 group"
            onClick={() => handleOAuthLogin('google')}
            disabled={isLoading}
          >
            {loadingProvider === 'google' ? (
              <Loader2 className="w-5 h-5 mr-3 animate-spin" />
            ) : (
              <svg className="w-5 h-5 mr-3 group-hover:scale-110 transition-transform" viewBox="0 0 24 24">
                <path
                  fill="#4285F4"
                  d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                />
                <path
                  fill="#34A853"
                  d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                />
                <path
                  fill="#FBBC05"
                  d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                />
                <path
                  fill="#EA4335"
                  d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                />
              </svg>
            )}
            <span className="text-foreground font-medium">Continue with Google</span>
          </Button>
        </div>

        <div className="mt-8 pt-6 border-t border-border/50">
          <p className="text-xs text-muted-foreground text-center leading-relaxed">
            By continuing, you agree to our{" "}
            <a href="#" className="text-primary hover:text-primary/80 underline transition-colors">
              Terms of Service
            </a>{" "}
            and{" "}
            <a href="#" className="text-primary hover:text-primary/80 underline transition-colors">
              Privacy Policy
            </a>
          </p>
        </div>
      </DialogContent>
    </Dialog>
  );
};