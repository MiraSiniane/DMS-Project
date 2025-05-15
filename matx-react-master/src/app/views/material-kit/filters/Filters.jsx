import { Box, FormControl, InputLabel, Select, MenuItem, Chip, IconButton } from "@mui/material";
import ClearIcon from "@mui/icons-material/Clear";
import SearchBar from "../searchbar/SearchBar";

const Filters = ({
  searchTerm,
  onSearch,
  filtersConfig,
  onFilterChange,
  onClearFilters,
}) => {
  return (
    <Box sx={{ display: "flex", gap: 2, mb: 3, alignItems: "center", pt: 2 }}>
      {/* Search Bar */}
      <SearchBar searchTerm={searchTerm} onSearch={onSearch} placeholder="Search..." />

      {/* Dynamic Filters */}
      {filtersConfig.map((filter) => (
        <FormControl key={filter.key} sx={{ minWidth: 120, mt: 1 }}>
          <InputLabel>{filter.label}</InputLabel>
          <Select
            multiple={filter.multiple}
            value={filter.value}
            onChange={(e) => onFilterChange(filter.key, e.target.value)}
            label={filter.label}
            renderValue={(selected) => (
              <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.5 }}>
                {selected.map((value) => (
                  <Chip key={value} label={value} />
                ))}
              </Box>
            )}
          >
            {filter.options.map((option) => (
              <MenuItem key={option.value} value={option.value}>
                {option.label}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      ))}

      {/* Clear Filters Button */}
      <IconButton
        onClick={onClearFilters}
        sx={{ alignSelf: "center", color: "primary.main", mt: 1 }}
        title="Clear Filters"
      >
        <ClearIcon />
      </IconButton>
    </Box>
  );
};

export default Filters;