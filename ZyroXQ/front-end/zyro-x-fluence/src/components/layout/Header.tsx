import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Logo } from "@/components/ui/logo";
import { AdvertiserSignupModal } from "@/components/ui/advertiser-signup-modal";

export const Header = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  return (
    <>
      <header className="absolute top-6 left-0 right-0 z-50 px-6 py-4">
        <nav className="max-w-7xl mx-auto flex items-center justify-between">
          <Logo size="md" />
          
          <div className="hidden md:flex items-center gap-8">
            <a href="#features" className="text-muted-foreground">
              Features
            </a>
            <a href="#how-it-works" className="text-muted-foreground">
              How It Works
            </a>
            <a href="#pricing" className="text-muted-foreground">
              Pricing
            </a>
            <a href="/contact" className="text-muted-foreground">
              Contact
            </a>
          </div>

          <div className="flex items-center gap-4">
            <Button 
              variant="hero" 
              size="sm"
              onClick={() => setIsModalOpen(true)}
            >
              Become Advertiser
            </Button>
          </div>
        </nav>
      </header>

      <AdvertiserSignupModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
      />
    </>
  );
};