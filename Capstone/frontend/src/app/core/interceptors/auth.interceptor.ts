import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('hw_jwt_token');

  const isBackendApi =
    req.url.includes('localhost:8080') ||
    req.url.startsWith('/') ||
    req.url.includes('http://localhost:8080');

  if (token && isBackendApi) {
    // Log only for debugging
    // console.log('Adding JWT token to request:', req.url);

    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    });
  } else if (!token && isBackendApi) {
    console.warn('No token available for request:', req.url);
  }

  return next(req);
};
