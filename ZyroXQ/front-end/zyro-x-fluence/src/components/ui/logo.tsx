import { cn } from "@/lib/utils";
import logoImage from "@/assest/Gradient_Icon_Map_Navigation_App_Logo__1_-removebg-preview (1).png";

interface LogoProps {
  className?: string;
  size?: "sm" | "md" | "lg";
}

export const Logo = ({ className, size = "md" }: LogoProps) => {

  return (
    <div className="flex items-center justify-center w-40 h-auto" >
    <img 
        src={logoImage}
        alt="ZyroXQ Logo"
        className={cn(
          "object-contain w-full h-full",
        )}
      />
    </div>
  );
};