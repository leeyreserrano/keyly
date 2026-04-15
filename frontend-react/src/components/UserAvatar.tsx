import { Avatar } from '@mui/material';
import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { API_BASE } from '../api/client';

export default function UserAvatar() {
  const { usuari, token } = useAuth();
  const [imgUrl, setImgUrl] = useState<string | null>(null);

  useEffect(() => {
    if (!usuari?.imatge || !token) return;

    const loadImage = async () => {
      try {
        const res = await fetch(
          `${API_BASE}/usuari/get/image/${usuari.imatge}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!res.ok) throw new Error('Error loading image');

        const blob = await res.blob();
        const url = URL.createObjectURL(blob);

        setImgUrl(url);
      } catch {
        setImgUrl(null);
      }
    };

    loadImage();

    return () => {
      if (imgUrl) URL.revokeObjectURL(imgUrl);
    };
  }, [usuari?.imatge, token]);

  const initial = usuari?.nom?.charAt(0).toUpperCase() ?? '?';

  return (
    <Avatar
      src={imgUrl ?? undefined}
      sx={{ width: 36, height: 36, fontWeight: 700 }}
    >
      {!imgUrl && initial}
    </Avatar>
  );
}