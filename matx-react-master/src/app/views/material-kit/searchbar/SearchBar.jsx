import { TextField } from "@mui/material";

const SearchBar = ({ searchTerm, onSearch, placeholder = "Search..." }) => {
  return (
    <TextField
      fullWidth
      placeholder={placeholder}
      value={searchTerm}
      onChange={onSearch}
      sx={{
        mb: 3,
        mt: 2,
        "& .MuiOutlinedInput-root": {
          // Target the root of the outlined input
          "& fieldset": {
            // Hide all borders
            borderBottom: "2px solid rgba(16, 117, 206, 0.57)", // Customize the bottom border color and thickness
            borderLeft: "none",
            borderRight: "none",
            borderTop: "none",
          },
          "&:hover fieldset": {
            // Hide borders on hover

            borderBottom: "2px solid primary", // Customize the bottom border color and thickness
            borderLeft: "none",
            borderRight: "none",
            borderTop: "none",
          },
          "&.Mui-focused fieldset": {
            // Show only the bottom border when focused
            borderBottom: "2px solid primary", // Customize the bottom border color and thickness
            borderLeft: "none",
            borderRight: "none",
            borderTop: "none",
          },
        },
      }}
    />
  );
};

export default SearchBar;