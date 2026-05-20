import { useEffect, useState } from 'react';
import { usuarisApi, type UsuariPublic, type UsuariAmbDepartament } from '../api/usuarisapi';
import { departamentsApi, type Departament } from '../api/departamentsapi';
import { useAuth } from '../context/AuthContext';
import type { Permisos } from '../api/compartitsapi';
import { importPublicKey, rsaDecrypt, rsaEncrypt } from '../crypto/cryptoService';
import { itemsApi } from '../api/itemsapi';
import { carpetasApi } from '../api/carpetasapi';
import { useCrypto } from '../context/CryptoContext';
import { compartitsApi } from '../api/compartitsapi';

export function useShareSelector() {
  const { usuari } = useAuth();
  const { privateKey } = useCrypto();
  const esAdmin = usuari?.rolIntern === 'ADMIN';
  const esCap = usuari?.rolIntern === 'CAP';
  const potVeureDepartaments = esAdmin || esCap;

  const [usuaris, setUsuaris] = useState<UsuariPublic[]>([]);
  const [usuarisAmbDept, setUsuarisAmbDept] = useState<UsuariAmbDepartament[]>([]);
  const [departaments, setDepartaments] = useState<Departament[]>([]);
  const [searchUsuaris, setSearchUsuaris] = useState('');
  const [searchDept, setSearchDept] = useState('');
  const [seleccionats, setSeleccionats] = useState<UsuariPublic[]>([]);
  const [departamentSeleccionat, setDepartamentSeleccionat] = useState<string>('');
  const [permisCompartir, setPermisCompartir] = useState<Permisos>('LECTURA');
  const [tab, setTab] = useState<'usuaris' | 'departament'>('usuaris');
  const [loadingUsuaris, setLoadingUsuaris] = useState(false);

  useEffect(() => {
    const load = async () => {
      setLoadingUsuaris(true);
      try {
        if (potVeureDepartaments) {
          const [tots, totsAmbDept, depts] = await Promise.all([
            usuarisApi.fetchAllPublic(),
            usuarisApi.fetchAllAmbDepartament(),
            departamentsApi.fetchAll(),
          ]);
          setUsuaris(tots.filter((u) => u.uuid !== usuari?.uuid));
          setUsuarisAmbDept(totsAmbDept.filter((u) => u.uuid !== usuari?.uuid));
          setDepartaments(depts);
        } else {
          const tots = await usuarisApi.fetchAllPublic();
          setUsuaris(tots.filter((u) => u.uuid !== usuari?.uuid));
        }
      } catch {}
      finally {
        setLoadingUsuaris(false);
      }
    };
    load();
  }, [usuari?.uuid, potVeureDepartaments]);

  const filtrats = usuaris.filter(
    (u) =>
      u.nom.toLowerCase().includes(searchUsuaris.toLowerCase()) ||
      u.correu.toLowerCase().includes(searchUsuaris.toLowerCase())
  );

  const departamentsFiltrats = departaments.filter((d) =>
    d.nom.toLowerCase().includes(searchDept.toLowerCase())
  );

  const usuarisDepartament = departamentSeleccionat
    ? usuarisAmbDept.filter((u) => u.departament?.uuid === departamentSeleccionat)
    : [];

  const toggleSeleccio = (u: UsuariPublic) => {
    setSeleccionats((prev) =>
      prev.some((s) => s.uuid === u.uuid)
        ? prev.filter((s) => s.uuid !== u.uuid)
        : [...prev, u]
    );
  };

  const handleSelectDepartament = (deptUuid: string) => {
    if (departamentSeleccionat === deptUuid) {
      setDepartamentSeleccionat('');
      setSeleccionats([]);
      return;
    }
    setDepartamentSeleccionat(deptUuid);
    const usuarisDelDept = usuarisAmbDept
      .filter((u) => u.departament?.uuid === deptUuid)
      .map((u) => ({
        uuid: u.uuid,
        nom: u.nom,
        correu: u.correu,
        imatge: u.imatge,
        publicKey: u.publicKey,
      }));
    setSeleccionats(usuarisDelDept);
  };

  const resetShare = () => {
    setSeleccionats([]);
    setDepartamentSeleccionat('');
    setSearchUsuaris('');
    setSearchDept('');
    setTab('usuaris');
    setPermisCompartir('LECTURA');
  };

  const compartirItem = async (itemUuid: string) => {
    if (!privateKey) throw new Error('Sessió criptogràfica expirada');
    if (seleccionats.length === 0) return;

    const item = await itemsApi.getItem(itemUuid);
    if (!item) throw new Error('Item no trobat');

    let dataKeyBytes: Uint8Array | null = null;
    if (item.encryptedDataKey?.encryptedDataKey && item.iv && item.contrasenya) {
      dataKeyBytes = await rsaDecrypt(privateKey, item.encryptedDataKey.encryptedDataKey);
    }

    const usuarisPayload = await Promise.all(
      seleccionats.map(async (receptor) => {
        if (dataKeyBytes && receptor.publicKey) {
          const pubKey = await importPublicKey(receptor.publicKey);
          const encryptedForReceptor = await rsaEncrypt(pubKey, dataKeyBytes);
          return {
            usuariUuid: receptor.uuid,
            permis: permisCompartir,
            encryptedDataKeys: [{ itemUuid, encryptedDataKey: encryptedForReceptor }],
          };
        }
        return { usuariUuid: receptor.uuid, permis: permisCompartir, encryptedDataKeys: [] };
      })
    );

    await compartitsApi.addCompartit({
      entitatUuid: itemUuid,
      tipusEntitat: 'ITEM',
      usuaris: usuarisPayload,
    });
  };

  const compartirCarpeta = async (carpetaUuid: string) => {
    if (!privateKey) throw new Error('Sessió criptogràfica expirada');
    if (seleccionats.length === 0) return;

    const carpeta = await carpetasApi.fetchItemsFromCarpeta(carpetaUuid);

    const itemsAmbDataKey = await Promise.all(
      carpeta
        .filter((item) => item.encryptedDataKey?.encryptedDataKey)
        .map(async (item) => {
          const dataKeyBytes = await rsaDecrypt(privateKey, item.encryptedDataKey!.encryptedDataKey);
          return { uuid: item.uuid, dataKeyBytes };
        })
    );

    const usuarisPayload = await Promise.all(
      seleccionats.map(async (receptor) => {
        if (!receptor.publicKey || itemsAmbDataKey.length === 0) {
          return { usuariUuid: receptor.uuid, permis: 'LECTURA' as Permisos, encryptedDataKeys: [] };
        }
        const pubKey = await importPublicKey(receptor.publicKey);
        const encryptedDataKeys = await Promise.all(
          itemsAmbDataKey.map(async ({ uuid, dataKeyBytes }) => {
            const encryptedForReceptor = await rsaEncrypt(pubKey, dataKeyBytes);
            return { itemUuid: uuid, encryptedDataKey: encryptedForReceptor };
          })
        );
        return { usuariUuid: receptor.uuid, permis: 'LECTURA' as Permisos, encryptedDataKeys };
      })
    );

    await compartitsApi.addCompartit({
      entitatUuid: carpetaUuid,
      tipusEntitat: 'CARPETA',
      usuaris: usuarisPayload,
    });
  };

  return {
    esAdmin,
    esCap,
    potVeureDepartaments,
    usuaris,
    usuarisAmbDept,
    departaments,
    filtrats,
    departamentsFiltrats,
    usuarisDepartament,
    searchUsuaris, setSearchUsuaris,
    searchDept, setSearchDept,
    seleccionats, setSeleccionats,
    departamentSeleccionat,
    permisCompartir, setPermisCompartir,
    tab, setTab,
    loadingUsuaris,
    toggleSeleccio,
    handleSelectDepartament,
    resetShare,
    compartirItem,
    compartirCarpeta,
  };
}