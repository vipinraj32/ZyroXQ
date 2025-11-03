# OAuth2 Integration Setup

## Issue Resolution: JWT Authentication Filter

**Problem:** Backend JWT filter is blocking OAuth2 login requests with "Invalid Header value!!"

**Solution:** The `/api/login` and `/api/auth/*` endpoints should be excluded from JWT authentication since users aren't logged in yet.

## Backend Configuration Required:

### 1. Update JWT Authentication Filter
Your `JwtAuthenticateFilter` should exclude these paths:
```java
// In your security config or filter
private static final String[] PUBLIC_ENDPOINTS = {
    "/api/login",
    "/api/auth/**",
    "/api/register",
    "/api/public/**"
};

// Skip JWT validation for public endpoints
if (isPublicEndpoint(request.getRequestURI())) {
    filterChain.doFilter(request, response);
    return;
}
```

### 2. Expected API Endpoints:

#### Primary Login Endpoint:
- `POST /api/login` (should be public, no JWT required)

#### OAuth2 Provider Endpoints:
- `GET /api/auth/google`
- `GET /api/auth/facebook` 
- `GET /api/auth/instagram`

### 3. Request/Response Flow:

#### Option A: API-Based Flow
```javascript
// Frontend sends:
POST /api/login
{
  "provider": "google",
  "action": "oauth_start"
}

// Backend responds:
{
  "authUrl": "https://accounts.google.com/oauth/authorize?..."
}
```

#### Option B: Direct Redirect Flow
```javascript
// Frontend redirects to:
GET /api/auth/google?returnUrl=http://localhost:5173/auth/callback
```

## Current Implementation:

The frontend now:
1. ✅ Removes JWT headers from OAuth2 requests
2. ✅ Uses `credentials: 'omit'` to avoid sending auth cookies
3. ✅ Provides fallback direct redirect if API fails
4. ✅ Handles both API-based and direct redirect flows

## Next Steps:

1. **Update your backend security config** to exclude OAuth2 endpoints from JWT validation
2. **Test the OAuth2 flow** - it should no longer show "Invalid Header value!!"
3. **Configure OAuth2 providers** in your backend (Google, Facebook, Instagram)

The frontend is ready and will work once the backend security filter is updated!