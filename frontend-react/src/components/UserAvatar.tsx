import { Avatar } from '@mui/material';
import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { API_BASE } from '../api/client';

interface UserAvatarProps {
  size?: number;
}

export default function UserAvatar({ size = 36 }: UserAvatarProps) {
  const { usuari, token } = useAuth();
  const [imgUrl, setImgUrl] = useState<string | null>(null);

  useEffect(() => {
    if (!usuari?.imatge || !token) return;

    let objectUrl: string | null = null;

    const loadImage = async () => {
      try {
        const res = await fetch(
          `${API_BASE}/usuari/get/image/${usuari.imatge}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );

        if (!res.ok) throw new Error('Error loading image');

        const blob = await res.blob();
        objectUrl = URL.createObjectURL(blob);
        setImgUrl(objectUrl);
      } catch {
        setImgUrl(null);
      }
    };

    loadImage();

    return () => {
      if (objectUrl) URL.revokeObjectURL(objectUrl);
    };
  }, [usuari?.imatge, token]);

  const initial = usuari?.nom?.charAt(0).toUpperCase() ?? '?';

  return (
    <Avatar
      src={imgUrl ?? undefined}
      sx={{ width: size, height: size, fontWeight: 700, fontSize: size * 0.4 }}
    >
      {!imgUrl && initial}
    </Avatar>
  );
}