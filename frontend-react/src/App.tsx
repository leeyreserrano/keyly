import { BrowserRouter, Routes, Route } from 'react-router';
import Login from './pages/Login';
import Home from './pages/Home';
import Item from './pages/Item';
import AddItem from './pages/AddItem';
import ChooseType from './pages/ChooseType';
import Items from './pages/Items';

import './App.css';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/home" element={<Home />} />
        <Route path="/Item" element={<Item />} />
        <Route path="/AddItem" element={<AddItem />} />
        <Route path="/ChooseType" element={<ChooseType />} />
        <Route path="/Items" element={<Items />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
