import { Avatar } from '@mui/material';
import { useEffect, useState, useRef } from 'react';
import { useAuth } from '../context/AuthContext';
import { API_BASE } from '../api/client';

interface UserAvatarProps {
  size?: number;
}

export default function UserAvatar({ size = 36 }: UserAvatarProps) {
  const { token } = useAuth();
  const [imgUrl, setImgUrl] = useState<string | null>(null);
  const objectUrlRef = useRef<string | null>(null);

  useEffect(() => {
    if (!token) return;

    const loadImage = async () => {
      try {
        const res = await fetch(`${API_BASE}/usuari/get/image`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!res.ok) throw new Error(`HTTP ${res.status}`);

        const blob = await res.blob();
        if (objectUrlRef.current) URL.revokeObjectURL(objectUrlRef.current);
        objectUrlRef.current = URL.createObjectURL(blob);
        setImgUrl(objectUrlRef.current);
      } catch (e) {
        console.error('UserAvatar fetch error:', e);
        setImgUrl(null);
      }
    };

    loadImage();

    return () => {
      if (objectUrlRef.current) {
        URL.revokeObjectURL(objectUrlRef.current);
        objectUrlRef.current = null;
      }
    };
  }, [token]);

  const { usuari } = useAuth();
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