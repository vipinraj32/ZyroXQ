import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Separator } from "@/components/ui/separator";
import { 
  PlayCircle, 
  Video, 
  Instagram, 
  TrendingUp, 
  Users, 
  DollarSign, 
  Plus,
  Eye,
  Clock,
  CheckCircle,
  PauseCircle,
  BarChart3,
  Settings,
  LogOut,
  User,
  Wallet,
  Home,
  Briefcase,
  Activity,
  Menu,
  X,
  ArrowLeft,
  Zap,
  Crown,
  Edit,
  MapPin,
  MessageSquare,
  Palette,
  FileText,
  HelpCircle
} from "lucide-react";

interface Campaign {
  id: string;
  title: string;
  type: 'story' | 'video' | 'reel';
  status: 'running' | 'completed' | 'paused';
  budget: number;
  spent: number;
  reach: number;
  engagement: number;
  startDate: string;
  endDate: string;
  description: string;
}

interface AdvertiserProfile {
  companyName: string;
  walletAddres: string | null;
  profileImage: string;
  imageName: string;
}

const mockCampaigns: Campaign[] = [
  {
    id: '1',
    title: 'Summer Fashion Campaign',
    type: 'story',
    status: 'running',
    budget: 5000,
    spent: 2300,
    reach: 45000,
    engagement: 8.5,
    startDate: '2024-10-01',
    endDate: '2024-10-31',
    description: 'Promoting new summer collection through Instagram stories'
  },
  {
    id: '2',
    title: 'Product Demo Videos',
    type: 'video',
    status: 'completed',
    budget: 8000,
    spent: 7800,
    reach: 120000,
    engagement: 12.3,
    startDate: '2024-09-01',
    endDate: '2024-09-30',
    description: 'Detailed product demonstration videos by tech influencers'
  }
];

export const AdvertiserDashboard = () => {
  const [userData, setUserData] = useState<any>(null);
  const [advertiserProfile, setAdvertiserProfile] = useState<AdvertiserProfile | null>(null);
  const [campaigns, setCampaigns] = useState<Campaign[]>(mockCampaigns);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [activeSection, setActiveSection] = useState("dashboard");

  // Fetch advertiser details
  const fetchAdvertiserDetails = async () => {
    try {
      const email = sessionStorage.getItem('email');
      if (!email) {
        throw new Error('Email not found in session');
      }

      const token = localStorage.getItem('token');
      const response = await fetch(`/api/advertiser/get-details?email=${encodeURIComponent(email)}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        const data = await response.json();
        setAdvertiserProfile(data);
      } else {
        throw new Error('Failed to fetch advertiser details');
      }
    } catch (error) {
      console.error('Error fetching advertiser details:', error);
      setError('Failed to load profile details');
    }
  };

  useEffect(() => {
    const initializeDashboard = async () => {
      const email = sessionStorage.getItem('email');
      const username = sessionStorage.getItem('username');
      const profileStatus = sessionStorage.getItem('profileStatus');

      if (!email || profileStatus !== 'true') {
        window.location.href = profileStatus === 'false' ? '/update-details' : '/login';
        return;
      }

      setUserData({ email, username });
      await fetchAdvertiserDetails();
      setIsLoading(false);
    };

    initializeDashboard();
  }, []);

  const handleLogout = () => {
    sessionStorage.clear();
    localStorage.clear();
    window.location.href = '/';
  };

  const getProfileImageUrl = () => {
    if (advertiserProfile?.profileImage) {
      return `data:image/png;base64,${advertiserProfile.profileImage}`;
    }
    return null;
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
          <p className="mt-4 text-muted-foreground">Loading dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-black flex">
      {/* Modern Sidebar - Fixed Position */}
      <div className="fixed left-0 top-0 h-screen w-64 bg-gray-900 border-r border-gray-700 flex flex-col z-50">
        
        {/* Sidebar Logo/Brand */}
        <div className="p-6 border-b border-gray-700 flex-shrink-0">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
              <Crown className="w-5 h-5 text-black" />
            </div>
            <div>
              <h3 className="font-bold text-white text-lg">ZyroXQ</h3>
              <p className="text-xs text-gray-400">Advertiser Portal</p>
            </div>
          </div>
        </div>

        {/* Navigation Sections */}
        <nav className="flex-1 px-4 py-6 overflow-y-auto">
          {/* Main Navigation */}
          <div className="space-y-1 mb-8">
            <div className="px-3 py-2">
              <h4 className="text-xs font-semibold text-gray-400 uppercase tracking-wider">Main</h4>
            </div>
            <button
              onClick={() => setActiveSection('dashboard')}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                activeSection === 'dashboard'
                  ? 'bg-primary text-black'
                  : 'text-gray-300 hover:bg-gray-800 hover:text-white'
              }`}
            >
              <Home className="w-5 h-5" />
              Dashboard
            </button>
            
            <button
              onClick={() => setActiveSection('campaigns')}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                activeSection === 'campaigns'
                  ? 'bg-primary text-black'
                  : 'text-gray-300 hover:bg-gray-800 hover:text-white'
              }`}
            >
              <Briefcase className="w-5 h-5" />
              Campaigns
            </button>

            <button
              onClick={() => setActiveSection('analytics')}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                activeSection === 'analytics'
                  ? 'bg-primary text-black'
                  : 'text-gray-300 hover:bg-gray-800 hover:text-white'
              }`}
            >
              <BarChart3 className="w-5 h-5" />
              Analytics
            </button>
          </div>

          {/* Tools Section */}
          <div className="space-y-1">
            <div className="px-3 py-2">
              <h4 className="text-xs font-semibold text-gray-400 uppercase tracking-wider">Tools</h4>
            </div>
            <button
              onClick={() => setActiveSection('chats')}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                activeSection === 'chats'
                  ? 'bg-primary text-black'
                  : 'text-gray-300 hover:bg-gray-800 hover:text-white'
              }`}
            >
              <MessageSquare className="w-5 h-5" />
              Chats
            </button>

            <button
              onClick={() => setActiveSection('background')}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                activeSection === 'background'
                  ? 'bg-primary text-black'
                  : 'text-gray-300 hover:bg-gray-800 hover:text-white'
              }`}
            >
              <Palette className="w-5 h-5" />
              Background
            </button>

            <button
              onClick={() => setActiveSection('apps')}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                activeSection === 'apps'
                  ? 'bg-primary text-black'
                  : 'text-gray-300 hover:bg-gray-800 hover:text-white'
              }`}
            >
              <Activity className="w-5 h-5" />
              Apps
            </button>

            <button
              onClick={() => setActiveSection('prompts')}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                activeSection === 'prompts'
                  ? 'bg-primary text-black'
                  : 'text-gray-300 hover:bg-gray-800 hover:text-white'
              }`}
            >
              <FileText className="w-5 h-5" />
              Prompts
            </button>
          </div>
        </nav>

        {/* Profile Section - Fixed at Bottom */}
        <div className="p-4 border-t border-gray-700 flex-shrink-0">
          <button
            onClick={() => setActiveSection('profile')}
            className={`w-full flex items-center gap-3 p-3 rounded-lg transition-colors ${
              activeSection === 'profile'
                ? 'bg-gray-800'
                : 'hover:bg-gray-800'
            }`}
          >
            <div className="relative">
              <Avatar className="w-10 h-10">
                <AvatarImage src={getProfileImageUrl() || undefined} alt="Profile" />
                <AvatarFallback className="bg-primary text-black font-semibold">
                  {advertiserProfile?.companyName?.charAt(0) || userData?.username?.charAt(0) || 'A'}
                </AvatarFallback>
              </Avatar>
              {/* Small logo badge */}
              <div className="absolute -bottom-1 -right-1 w-4 h-4 bg-primary rounded-full flex items-center justify-center">
                {getProfileImageUrl() ? (
                  <img src={getProfileImageUrl() || undefined} alt="Logo" className="w-3 h-3 rounded-full object-cover" />
                ) : (
                  <span className="text-black font-bold text-xs">{advertiserProfile?.companyName?.charAt(0) || 'C'}</span>
                )}
              </div>
            </div>
            <div className="flex-1 text-left min-w-0">
              <p className="text-white font-medium text-sm truncate">
                {advertiserProfile?.companyName || userData?.username || 'Advertiser'}
              </p>
              <p className="text-gray-400 text-xs truncate">{userData?.email}</p>
            </div>
          </button>
        </div>
      </div>

      {/* Main Content - With Left Margin */}
      <div className="flex-1 ml-64 bg-black">
        <main className="p-6">
          {error && (
            <div className="mb-6 p-4 bg-destructive/10 border border-destructive/20 rounded-lg">
              <p className="text-destructive">{error}</p>
            </div>
          )}

          {activeSection === 'dashboard' && (
            <div className="text-white">
              <div className="mb-8">
                <h1 className="text-3xl font-bold text-white mb-2">
                  Welcome back, {advertiserProfile?.companyName || userData?.username}!
                </h1>
                <p className="text-gray-400">
                  Here's an overview of your advertising campaigns and performance.
                </p>
              </div>

              {/* Quick Action Buttons */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
                <Button 
                  variant="outline" 
                  size="lg" 
                  className="h-20 flex-col gap-2 bg-gray-950/50 border-gray-800 hover:bg-gray-900 text-white"
                >
                  <Instagram className="w-6 h-6" />
                  <span>Story Promotion</span>
                </Button>

                <Button 
                  variant="outline" 
                  size="lg" 
                  className="h-20 flex-col gap-2 bg-gray-950/50 border-gray-800 hover:bg-gray-900 text-white"
                >
                  <Video className="w-6 h-6" />
                  <span>Video Promotion</span>
                </Button>

                <Button 
                  variant="outline" 
                  size="lg" 
                  className="h-20 flex-col gap-2 bg-gray-950/50 border-gray-800 hover:bg-gray-900 text-white"
                >
                  <PlayCircle className="w-6 h-6" />
                  <span>Reel Promotion</span>
                </Button>
              </div>
            </div>
          )}

          {activeSection === 'profile' && advertiserProfile && (
            <div className="min-h-screen bg-black">{/* Header */}
              <div className="flex items-center justify-between p-6 border-b border-gray-800">
                <div className="flex items-center gap-4">
                  <Button 
                    variant="ghost" 
                    size="sm"
                    onClick={() => setActiveSection('dashboard')}
                    className="text-gray-400 hover:text-white"
                  >
                    <ArrowLeft className="w-5 h-5" />
                  </Button>
                  <div>
                    <h1 className="text-2xl font-bold text-white">My Profile</h1>
                    <p className="text-gray-400 text-sm">Manage your account, subscription, and preferences</p>
                  </div>
                </div>
                <Button 
                  variant="outline" 
                  onClick={handleLogout}
                  className="border-gray-700 text-gray-300 hover:bg-gray-800"
                >
                  <LogOut className="w-4 h-4 mr-2" />
                  Logout
                </Button>
              </div>

              <div className="p-6 max-w-4xl mx-auto space-y-6">
                {/* PRO Upgrade Card */}
                <Card className="bg-gradient-to-r from-primary/20 to-green-500/20 border-primary/30">
                  <CardContent className="p-6">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div className="p-2 bg-primary/20 rounded-lg">
                          <Zap className="w-6 h-6 text-primary" />
                        </div>
                        <div>
                          <h3 className="text-xl font-bold text-white">Unlock More with PRO</h3>
                          <p className="text-gray-400">Get advanced features and unlimited campaigns</p>
                        </div>
                      </div>
                      <Button className="bg-primary hover:bg-primary/90 text-black font-semibold">
                        <Crown className="w-4 h-4 mr-2" />
                        Upgrade to PRO
                      </Button>
                    </div>
                  </CardContent>
                </Card>

                {/* Profile Card */}
                <Card className="bg-gray-800/50 border-gray-700">
                  <CardContent className="p-8">
                    <div className="flex items-start justify-between mb-8">
                      <div className="flex items-center gap-6">
                        <div className="relative">
                          <Avatar className="w-20 h-20 border-4 border-gray-700">
                            <AvatarImage src={getProfileImageUrl() || undefined} alt="Company Logo" />
                            <AvatarFallback className="bg-primary text-black text-2xl font-bold">
                              {advertiserProfile.companyName?.charAt(0) || 'C'}
                            </AvatarFallback>
                          </Avatar>
                          {/* Profile logo at bottom */}
                          <div className="absolute -bottom-2 -right-2 w-8 h-8 bg-primary rounded-full flex items-center justify-center">
                            {getProfileImageUrl() ? (
                              <img src={getProfileImageUrl() || undefined} alt="Logo" className="w-6 h-6 rounded-full object-cover" />
                            ) : (
                              <span className="text-black font-bold text-sm">{advertiserProfile.companyName?.charAt(0) || 'C'}</span>
                            )}
                          </div>
                        </div>
                        <div>
                          <h2 className="text-2xl font-bold text-white mb-1">{advertiserProfile.companyName}</h2>
                          <p className="text-gray-400 mb-2">{userData?.email}</p>
                          <div className="flex items-center gap-2">
                            <div className="flex items-center gap-1 text-primary">
                              <Zap className="w-4 h-4" />
                              <span className="text-sm font-medium">Basic Plan</span>
                            </div>
                          </div>
                          <p className="text-gray-500 text-sm mt-1">Credits reset in 3 hours and 31 minutes</p>
                        </div>
                      </div>
                      <Button variant="outline" className="border-gray-600 text-gray-300 hover:bg-gray-700">
                        <Edit className="w-4 h-4 mr-2" />
                        Edit Profile
                      </Button>
                    </div>

                    {/* Profile Details */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                      {/* Username */}
                      <div className="space-y-2">
                        <div className="flex items-center gap-2 text-gray-400">
                          <User className="w-4 h-4" />
                          <span className="text-sm">Username</span>
                        </div>
                        <p className="text-white font-medium">@{advertiserProfile.companyName?.toLowerCase().replace(/\s+/g, '') || 'company'}</p>
                      </div>

                      {/* Timezone */}
                      <div className="space-y-2">
                        <div className="flex items-center gap-2 text-gray-400">
                          <MapPin className="w-4 h-4" />
                          <span className="text-sm">Timezone</span>
                        </div>
                        <p className="text-white font-medium">Asia/Calcutta</p>
                      </div>

                      {/* Wallet */}
                      <div className="space-y-2">
                        <div className="flex items-center gap-2 text-gray-400">
                          <Wallet className="w-4 h-4" />
                          <span className="text-sm">Wallet Status</span>
                        </div>
                        <div className="flex items-center gap-2">
                          <p className="text-white font-medium">
                            {advertiserProfile.walletAddres ? 'Connected' : 'Not Connected'}
                          </p>
                          {advertiserProfile.walletAddres && (
                            <div className="w-2 h-2 bg-green-500 rounded-full"></div>
                          )}
                        </div>
                      </div>

                      {/* Plan */}
                      <div className="space-y-2">
                        <div className="flex items-center gap-2 text-gray-400">
                          <Crown className="w-4 h-4" />
                          <span className="text-sm">Subscription</span>
                        </div>
                        <p className="text-white font-medium">Basic Plan</p>
                      </div>
                    </div>
                  </CardContent>
                </Card>

                {/* Security Settings */}
                <Card className="bg-gray-800/50 border-gray-700">
                  <CardHeader className="pb-4">
                    <CardTitle className="text-white">Security Settings</CardTitle>
                    <CardDescription className="text-gray-400">
                      Manage your account security and password settings
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <Button variant="outline" className="border-gray-600 text-gray-300 hover:bg-gray-700">
                      <Settings className="w-4 h-4 mr-2" />
                      Change Password
                    </Button>
                  </CardContent>
                </Card>
              </div>
            </div>
          )}

          {activeSection === 'campaigns' && (
            <div className="text-white">
              <h1 className="text-3xl font-bold mb-6">Campaign Management</h1>
              <Card className="bg-gray-800/50 border-gray-700">
                <CardContent className="p-8 text-center">
                  <Briefcase className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                  <h3 className="text-lg font-semibold text-white mb-2">Campaign Management</h3>
                  <p className="text-gray-400">Campaign management features coming soon.</p>
                </CardContent>
              </Card>
            </div>
          )}

          {activeSection === 'analytics' && (
            <div className="text-white">
              <h1 className="text-3xl font-bold mb-6">Analytics & Insights</h1>
              <Card className="bg-gray-800/50 border-gray-700">
                <CardContent className="p-8 text-center">
                  <BarChart3 className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                  <h3 className="text-lg font-semibold text-white mb-2">Analytics Dashboard</h3>
                  <p className="text-gray-400">Detailed analytics and insights coming soon.</p>
                </CardContent>
              </Card>
            </div>
          )}

          {activeSection === 'chats' && (
            <div className="text-white">
              <h1 className="text-3xl font-bold mb-6">Chats</h1>
              <Card className="bg-gray-800/50 border-gray-700">
                <CardContent className="p-8 text-center">
                  <MessageSquare className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                  <h3 className="text-lg font-semibold text-white mb-2">Chat Management</h3>
                  <p className="text-gray-400">Manage your conversations and communications.</p>
                </CardContent>
              </Card>
            </div>
          )}

          {activeSection === 'background' && (
            <div className="text-white">
              <h1 className="text-3xl font-bold mb-6">Background</h1>
              <Card className="bg-gray-800/50 border-gray-700">
                <CardContent className="p-8 text-center">
                  <Palette className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                  <h3 className="text-lg font-semibold text-white mb-2">Background Settings</h3>
                  <p className="text-gray-400">Customize your workspace background and themes.</p>
                </CardContent>
              </Card>
            </div>
          )}

          {activeSection === 'apps' && (
            <div className="text-white">
              <h1 className="text-3xl font-bold mb-6">Apps</h1>
              <Card className="bg-gray-800/50 border-gray-700">
                <CardContent className="p-8 text-center">
                  <Activity className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                  <h3 className="text-lg font-semibold text-white mb-2">App Integrations</h3>
                  <p className="text-gray-400">Connect and manage your third-party applications.</p>
                </CardContent>
              </Card>
            </div>
          )}

          {activeSection === 'prompts' && (
            <div className="text-white">
              <h1 className="text-3xl font-bold mb-6">Prompts</h1>
              <Card className="bg-gray-800/50 border-gray-700">
                <CardContent className="p-8 text-center">
                  <FileText className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                  <h3 className="text-lg font-semibold text-white mb-2">AI Prompts</h3>
                  <p className="text-gray-400">Manage your AI prompts and templates.</p>
                </CardContent>
              </Card>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default AdvertiserDashboard;