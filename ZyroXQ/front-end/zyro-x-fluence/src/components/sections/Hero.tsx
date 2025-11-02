import { useState } from "react";
import { Button } from "@/components/ui/button";
import { LoginModal } from "@/components/ui/login-modal";
import { ArrowRight, Zap, Shield, TrendingUp } from "lucide-react";

export const Hero = () => {
  const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);

  return (
    <>
      <section className="relative min-h-screen flex items-center justify-center px-6 py-20 overflow-hidden">
      {/* Background elements */}
      <div className="absolute inset-0 overflow-hidden">
        <div className="absolute top-1/4 left-1/4 w-64 h-64 bg-primary/10 blur-3xl" 
             style={{
               clipPath: 'polygon(0% 0%, 100% 0%, 100% 20%, 20% 80%, 100% 80%, 100% 100%, 0% 100%, 0% 80%, 80% 20%, 0% 20%)'
             }} />
        <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-primary-glow/5 blur-3xl"
             style={{
               clipPath: 'polygon(0% 0%, 100% 0%, 100% 15%, 15% 85%, 100% 85%, 100% 100%, 0% 100%, 0% 85%, 85% 15%, 0% 15%)'
             }} />
        <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] border border-primary/20"
             style={{
               clipPath: 'polygon(0% 0%, 100% 0%, 100% 25%, 25% 75%, 100% 75%, 100% 100%, 0% 100%, 0% 75%, 75% 25%, 0% 25%)'
             }} />
      </div>

      <div className="relative z-10 max-w-4xl mx-auto text-center mt-20">
        {/* Eyebrow text */}
        <div className="inline-flex items-center gap-2 px-4 py-2 mb-4 glass-morphism rounded-full">
          <Zap className="w-3 h-3 text-primary" />
          <span className="text-sm font-medium text-muted-foreground">
            Introducing the Future of Influencer Marketing
          </span>
        </div>

        {/* Main headline */}
        <h1 className="text-xl md:text-2xl lg:text-5xl font-bold mb-2 leading-tight">
          <span className="text-foreground">Stop Guessing Ads,</span>
          <br />
          <span className="text-gradient">Start Trusting Influence</span>
        </h1>

        {/* Subheadline */}
        <p className="text-base md:text-lg lg:text-xl text-muted-foreground mb-12 max-w-3xl mx-auto leading-relaxed">
          Connect brands and influencers with transparency, automation, and trust.
          <br />
          <span className="text-primary font-medium">Smart contracts ensure payments. Decentralized storage secures data. No middlemen, just real growth.</span>
        </p>

        {/* CTA Buttons */}
        <div className="flex flex-col sm:flex-row items-center justify-center gap-4 mb-12">
          <Button 
            variant="hero" 
            size="lg" 
            className="min-w-[190px]"
            onClick={() => setIsLoginModalOpen(true)}
          >
            Try ZyroXQ Now
          </Button>
        </div>
      </div>
    </section>

    {/* Login Modal */}
    <LoginModal 
      open={isLoginModalOpen} 
      onOpenChange={setIsLoginModalOpen} 
    />
    </>
  );
};