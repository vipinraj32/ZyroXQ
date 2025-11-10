import { Card } from "@/components/ui/card";
import { Shield, Zap, TrendingUp, Users, DollarSign, Globe } from "lucide-react";

const features = [
  {
    icon: Shield,
    title: "Smart Contract Security",
    description: "Automated escrow payments protected by blockchain technology. No disputes, no delays."
  },
  {
    icon: Zap,
    title: "AI-Powered Matching",
    description: "Advanced algorithms connect brands with the perfect influencers based on audience, engagement, and niche."
  },
  {
    icon: TrendingUp,
    title: "Real-time Analytics",
    description: "Track campaign performance, ROI, and engagement metrics in real-time with detailed reporting."
  },
  {
    icon: Users,
    title: "Transparent Reviews",
    description: "Decentralized review system ensures authentic feedback and builds trust in the ecosystem."
  },
  {
    icon: DollarSign,
    title: "Instant Payments",
    description: "Get paid immediately upon campaign completion. No waiting periods, no payment delays."
  },
  {
    icon: Globe,
    title: "Global Reach",
    description: "Connect with influencers and brands worldwide. Break geographical barriers and scale globally."
  }
];

export const Features = () => {
  return (
    <section id="features" className="py-20 px-6">
      <div className="max-w-7xl mx-auto">
        <div className="text-center mb-16">
          <h2 className="text-3xl md:text-5xl font-bold mb-6">
            <span className="text-foreground">Why Choose </span>
            <span className="text-gradient">ZyroXQ?</span>
          </h2>
          <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
            Built for the future of influencer marketing with cutting-edge technology and user-first design.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {features.map((feature, index) => (
            <Card key={index} className="p-6 bg-gradient-card border-border/50">
              <div className="flex items-start gap-4">
                <div className="p-3 rounded-lg bg-primary/10 border border-primary/20">
                  <feature.icon className="w-6 h-6 text-primary" />
                </div>
                <div>
                  <h3 className="text-xl font-semibold mb-2 text-foreground">
                    {feature.title}
                  </h3>
                  <p className="text-muted-foreground leading-relaxed">
                    {feature.description}
                  </p>
                </div>
              </div>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
};