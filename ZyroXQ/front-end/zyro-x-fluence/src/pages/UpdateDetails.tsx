import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Logo } from "@/components/ui/logo";
import { Upload } from "lucide-react";

interface AdvertiserFormData {  
  companyName: string;
  companyLogo: File | null;
}

interface InfluencerFormData {
  fullName: string;
  phone: string;
  bio: string;
  location: string;
  category: string;
  socialMediaLinks: {
    instagram: string;
    youtube: string;
    tiktok: string;
    twitter: string;
  };
}

export const UpdateDetails = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [userData, setUserData] = useState<any>(null);
  const [userRole, setUserRole] = useState<string>("");

  // Advertiser form data - simplified
  const [advertiserData, setAdvertiserData] = useState<AdvertiserFormData>({
    companyName: "",
    companyLogo: null,
  });

  // Influencer form data
  const [influencerData, setInfluencerData] = useState<InfluencerFormData>({
    fullName: "",
    phone: "",
    bio: "",
    location: "",
    category: "",
    socialMediaLinks: {
      instagram: "",
      youtube: "",
      tiktok: "",
      twitter: ""
    }
  });

  useEffect(() => {
    // Get user data from session storage
    const storedUserData = sessionStorage.getItem('userData');
    const profileStatus = sessionStorage.getItem('profileStatus');
    const role = sessionStorage.getItem('role');
    
    if (storedUserData && profileStatus === 'false') {
      const parsedData = JSON.parse(storedUserData);
      setUserData(parsedData);
      setUserRole(role || "");
      
      // Pre-fill data for influencer
      if (role === '[ROLE_INFLUNCER]' && parsedData.username) {
        setInfluencerData(prev => ({ ...prev, fullName: parsedData.username }));
      }
    } else {
      // Redirect to login or home if profile is complete or no data
      window.location.href = profileStatus === 'true' ? '/' : '/login';
    }
  }, []);

  // Handle form submission for influencer
  const handleInfluencerSubmit = async () => {
    setIsLoading(true);
    setError("");

    try {
      const token = localStorage.getItem('token');
      const response = await fetch('/api/user/update-profile', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(influencerData),
      });

      if (response.ok) {
        const updatedData = await response.json();
        console.log('Influencer profile updated:', updatedData);
        
        // Update session data
        sessionStorage.setItem('profileStatus', 'true');
        // Redirect influencers to home page for now (could be influencer dashboard later)
        window.location.href = '/';
      } else {
        const errorData = await response.json();
        setError(errorData.message || 'Failed to update profile');
      }
    } catch (error) {
      setError('Network error. Please try again.');
      console.error('Error updating profile:', error);
    } finally {
      setIsLoading(false);
    }
  };

  // Handle form submission for advertiser - updated to match backend API
  const handleAdvertiserSubmit = async () => {
    setIsLoading(true);
    setError("");

    try {
      // Validation: check required fields
      if (!advertiserData.companyName.trim()) {
        setError('Company name is required');
        setIsLoading(false);
        return;
      }

      if (!advertiserData.companyLogo) {
        setError('Company logo is required');
        setIsLoading(false);
        return;
      }

      // Get email from session storage
      const email = sessionStorage.getItem('email');
      if (!email) {
        setError('Email not found. Please login again.');
        setIsLoading(false);
        return;
      }

      const token = localStorage.getItem('token');
      const formData = new FormData();
      
      // Add required parameters as per backend API
      formData.append('companyName', advertiserData.companyName);
      formData.append('email', email);
      formData.append('file', advertiserData.companyLogo);

      const response = await fetch('/api/advertiser/update-details', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData,
      });

      if (response.ok) {
        const updatedData = await response.json();
        console.log('Advertiser profile updated:', updatedData);
        
        // Update session data
        sessionStorage.setItem('profileStatus', 'true');
        // Redirect to advertiser dashboard after successful profile completion
        window.location.href = '/advertiser-dashboard';
      } else {
        let errorMessage = 'Failed to update profile';
        try {
          const errorData = await response.json();
          errorMessage = errorData.message || errorData.error || errorMessage;
        } catch (parseError) {
          console.error('Error parsing response:', parseError);
        }
        setError(errorMessage);
      }
    } catch (error) {
      setError('Network error. Please try again.');
      console.error('Error updating profile:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleInputChange = (field: string, value: string | number | File | null) => {
    if (userRole === '[ROLE_ADVERTISER]') {
      setAdvertiserData(prev => ({ ...prev, [field]: value }));
    } else {
      if (field.startsWith('social.')) {
        const socialField = field.split('.')[1];
        setInfluencerData(prev => ({
          ...prev,
          socialMediaLinks: {
            ...prev.socialMediaLinks,
            [socialField]: value as string
          }
        }));
      } else {
        setInfluencerData(prev => ({ ...prev, [field]: value }));
      }
    }
    
    // Clear error when user starts typing
    if (error) setError("");
  };

  if (!userData) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
          <p className="mt-4 text-muted-foreground">Loading...</p>
        </div>
      </div>
    );
  }

  // Render influencer form
  if (userRole === '[ROLE_INFLUNCER]') {
    return (
      <div className="min-h-screen bg-background">
        <div className="container mx-auto px-4 py-8">
          <div className="max-w-2xl mx-auto">
            {/* Header */}
            <div className="text-center mb-8">
              <div className="flex justify-center mb-4">
                <Logo size="md" />
              </div>
              <h1 className="text-2xl font-bold text-foreground">Complete Your Influencer Profile</h1>
              <p className="text-muted-foreground">Welcome {userData.username}! Please complete your profile to get started.</p>
            </div>

            <div className="bg-card p-8 rounded-lg border border-border shadow-lg">
              <form onSubmit={(e) => { e.preventDefault(); handleInfluencerSubmit(); }} className="space-y-6">
                {error && (
                  <div className="p-3 bg-destructive/10 border border-destructive/20 rounded-md">
                    <p className="text-destructive text-sm">{error}</p>
                  </div>
                )}

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="fullName" className="text-foreground">Full Name</Label>
                    <Input
                      id="fullName"
                      type="text"
                      placeholder="Enter your full name"
                      value={influencerData.fullName}
                      onChange={(e) => handleInputChange("fullName", e.target.value)}
                      className="bg-background border-border text-foreground"
                      required
                      disabled={isLoading}
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="phone" className="text-foreground">Phone Number</Label>
                    <Input
                      id="phone"
                      type="tel"
                      placeholder="Enter your phone number"
                      value={influencerData.phone}
                      onChange={(e) => handleInputChange("phone", e.target.value)}
                      className="bg-background border-border text-foreground"
                      required
                      disabled={isLoading}
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="bio" className="text-foreground">Bio</Label>
                  <Textarea
                    id="bio"
                    placeholder="Tell us about yourself..."
                    value={influencerData.bio}
                    onChange={(e) => handleInputChange("bio", e.target.value)}
                    className="bg-background border-border text-foreground"
                    rows={3}
                    disabled={isLoading}
                  />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="location" className="text-foreground">Location</Label>
                    <Input
                      id="location"
                      type="text"
                      placeholder="City, Country"
                      value={influencerData.location}
                      onChange={(e) => handleInputChange("location", e.target.value)}
                      className="bg-background border-border text-foreground"
                      disabled={isLoading}
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="category" className="text-foreground">Category</Label>
                    <Select onValueChange={(value) => handleInputChange("category", value)} disabled={isLoading}>
                      <SelectTrigger className="bg-background border-border text-foreground">
                        <SelectValue placeholder="Select category" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="lifestyle">Lifestyle</SelectItem>
                        <SelectItem value="tech">Technology</SelectItem>
                        <SelectItem value="fashion">Fashion</SelectItem>
                        <SelectItem value="gaming">Gaming</SelectItem>
                        <SelectItem value="fitness">Fitness</SelectItem>
                        <SelectItem value="food">Food</SelectItem>
                        <SelectItem value="travel">Travel</SelectItem>
                        <SelectItem value="business">Business</SelectItem>
                        <SelectItem value="other">Other</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>

                <div className="space-y-4">
                  <Label className="text-foreground text-base font-semibold">Social Media Links</Label>
                  
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="instagram" className="text-foreground">Instagram</Label>
                      <Input
                        id="instagram"
                        type="url"
                        placeholder="https://instagram.com/username"
                        value={influencerData.socialMediaLinks.instagram}
                        onChange={(e) => handleInputChange("social.instagram", e.target.value)}
                        className="bg-background border-border text-foreground"
                        disabled={isLoading}
                      />
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="youtube" className="text-foreground">YouTube</Label>
                      <Input
                        id="youtube"
                        type="url"
                        placeholder="https://youtube.com/channel/..."
                        value={influencerData.socialMediaLinks.youtube}
                        onChange={(e) => handleInputChange("social.youtube", e.target.value)}
                        className="bg-background border-border text-foreground"
                        disabled={isLoading}
                      />
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="tiktok" className="text-foreground">TikTok</Label>
                      <Input
                        id="tiktok"
                        type="url"
                        placeholder="https://tiktok.com/@username"
                        value={influencerData.socialMediaLinks.tiktok}
                        onChange={(e) => handleInputChange("social.tiktok", e.target.value)}
                        className="bg-background border-border text-foreground"
                        disabled={isLoading}
                      />
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="twitter" className="text-foreground">Twitter</Label>
                      <Input
                        id="twitter"
                        type="url"
                        placeholder="https://twitter.com/username"
                        value={influencerData.socialMediaLinks.twitter}
                        onChange={(e) => handleInputChange("social.twitter", e.target.value)}
                        className="bg-background border-border text-foreground"
                        disabled={isLoading}
                      />
                    </div>
                  </div>
                </div>

                <div className="flex justify-center pt-4">
                  <Button 
                    type="submit" 
                    className="w-full bg-primary hover:bg-primary/90 text-primary-foreground"
                    size="lg"
                    disabled={isLoading}
                  >
                    {isLoading ? "Updating Profile..." : "Complete Profile"}
                  </Button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Render advertiser form - SIMPLIFIED TO SINGLE PAGE
  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto px-4 py-8">
        <div className="max-w-md mx-auto">
          {/* Header */}
          <div className="text-center mb-8">
            <div className="flex justify-center mb-4">
              <Logo size="md" />
            </div>
            <h1 className="text-2xl font-bold text-foreground">Company Details</h1>
            <p className="text-muted-foreground">Welcome {userData.username}! Please enter your company information.</p>
          </div>

          <div className="bg-card p-8 rounded-lg border border-border shadow-lg">
            {error && (
              <div className="p-3 bg-destructive/10 border border-destructive/20 rounded-md mb-6">
                <p className="text-destructive text-sm">{error}</p>
              </div>
            )}

            <form onSubmit={(e) => { e.preventDefault(); handleAdvertiserSubmit(); }} className="space-y-6">
              {/* Company Name */}
              <div className="space-y-2">
                <Label htmlFor="companyName" className="text-foreground font-medium">Company Name *</Label>
                <Input
                  id="companyName"
                  type="text"
                  placeholder="Enter your company name"
                  value={advertiserData.companyName}
                  onChange={(e) => handleInputChange("companyName", e.target.value)}
                  className="bg-background border-border text-foreground"
                  required
                  disabled={isLoading}
                />
              </div>

              {/* Company Logo */}
              <div className="space-y-2">
                <Label htmlFor="companyLogo" className="text-foreground font-medium">Company Logo</Label>
                <div className="space-y-2">
                  <div className="flex items-center gap-4">
                    <Input
                      id="companyLogo"
                      type="file"
                      accept="image/*"
                      onChange={(e) => handleInputChange("companyLogo", e.target.files?.[0] || null)}
                      className="bg-background border-border text-foreground flex-1"
                      disabled={isLoading}
                    />
                    <Upload className="w-5 h-5 text-muted-foreground" />
                  </div>
                  <p className="text-xs text-muted-foreground">Upload your company logo (PNG, JPG, JPEG)</p>
                  {advertiserData.companyLogo && (
                    <p className="text-xs text-green-600">
                      ✓ {advertiserData.companyLogo.name} selected
                    </p>
                  )}
                </div>
              </div>

              {/* Submit Button */}
              <Button 
                type="submit" 
                className="w-full bg-primary hover:bg-primary/90 text-primary-foreground"
                size="lg"
                disabled={isLoading || !advertiserData.companyName}
              >
                {isLoading ? "Updating Profile..." : "Complete Profile"}
              </Button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};  