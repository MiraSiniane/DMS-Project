// src/components/SearchBar.tsx
import type { ChangeEvent } from 'react';

interface SearchBarProps {
  onSearch: (query: string) => void;
}

export default function SearchBar({ onSearch }: SearchBarProps) {
  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    onSearch(e.target.value.toLowerCase());
  };

  return (
    <div className="search-bar">
      <input
        type="text"
        placeholder="Search users..."
        onChange={handleChange}
        className="search-input"
      />
    </div>
  );
}