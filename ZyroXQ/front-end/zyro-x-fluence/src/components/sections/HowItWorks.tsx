import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { ArrowRight, UserPlus, Search, Handshake, BarChart3 } from "lucide-react";

const steps = [
  {
    icon: UserPlus,
    title: "Create Your Profile",
    description: "Sign up as an advertiser or influencer. Complete your profile with authentic details and connect your wallet.",
    color: "from-blue-500 to-cyan-500"
  },
  {
    icon: Search,
    title: "Smart Matching",
    description: "Our AI analyzes your requirements and matches you with the perfect partners based on audience and engagement.",
    color: "from-purple-500 to-pink-500"
  },
  {
    icon: Handshake,
    title: "Secure Collaboration",
    description: "Smart contracts automatically handle payments and deliverables. Stake ETH to ensure commitment from both parties.",
    color: "from-green-500 to-emerald-500"
  },
  {
    icon: BarChart3,
    title: "Track & Grow",
    description: "Monitor real-time performance metrics and receive instant payments upon successful campaign completion.",
    color: "from-orange-500 to-red-500"
  }
];

export const HowItWorks = () => {
  return (
    <section id="how-it-works" className="py-20 px-6">
      <div className="max-w-7xl mx-auto">
        <div className="text-center mb-16">
          <h2 className="text-3xl md:text-5xl font-bold mb-6">
            <span className="text-foreground">How </span>
            <span className="text-gradient">ZyroXQ Works</span>
          </h2>
          <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
            Simple, secure, and transparent process designed for modern influencer marketing.
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-4 gap-8 mb-12">
          {steps.map((step, index) => (
            <div key={index} className="relative">
              <Card className="p-6 bg-gradient-card border-border/50 text-center h-full">
                <div className="relative mb-6">
                  <div className={`w-16 h-16 mx-auto rounded-full bg-gradient-to-r ${step.color} p-4 shadow-glow`}>
                    <step.icon className="w-8 h-8 text-white" />
                  </div>
                  <div className="absolute -top-2 -right-2 w-8 h-8 bg-primary text-primary-foreground rounded-full flex items-center justify-center text-sm font-bold">
                    {index + 1}
                  </div>
                </div>
                
                <h3 className="text-xl font-semibold mb-3 text-foreground">
                  {step.title}
                </h3>
                <p className="text-muted-foreground leading-relaxed">
                  {step.description}
                </p>
              </Card>
              
              {/* Connector arrow for larger screens */}
              {index < steps.length - 1 && (
                <div className="hidden xl:block absolute top-1/2 -right-4 transform -translate-y-1/2 z-10">
                  <ArrowRight className="w-8 h-8 text-primary" />
                </div>
              )}
            </div>
          ))}
        </div>

        <div className="text-center">
          <Button variant="hero" size="xl">
            Start Your Journey
            <ArrowRight className="w-5 h-5" />
          </Button>
        </div>
      </div>
    </section>
  );
};