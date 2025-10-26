import { useEffect, useState } from 'react';

export function useAppLoading() {
  const [isLoaded, setIsLoaded] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadAppResources() {
      try {
        // Wait for critical resources to be available
        // This includes waiting for the JavaScript bundle to be fully parsed
        // and for any heavy native modules to initialize
        
        // Give time for Reanimated worklets to initialize
        await new Promise(resolve => setTimeout(resolve, 50));
        
        // Ensure the main thread is not blocked
        await new Promise(resolve => {
          const checkReady = () => {
            // Simple check to ensure the app is responsive
            const start = Date.now();
            setTimeout(() => {
              const delay = Date.now() - start;
              if (delay < 100) {
                // Main thread is responsive
                resolve(true);
              } else {
                // Main thread might be blocked, wait a bit more
                setTimeout(checkReady, 50);
              }
            }, 10);
          };
          checkReady();
        });

        setIsLoaded(true);
      } catch (e) {
        console.error('Error loading app resources:', e);
        setError(e instanceof Error ? e.message : 'Unknown error');
        // Even if there's an error, we should still show the app
        setIsLoaded(true);
      }
    }

    loadAppResources();
  }, []);

  return { isLoaded, error };
}