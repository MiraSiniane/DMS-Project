// app/components/FloatingButton.jsx
import { Fab } from "@mui/material";
import AddIcon from "@mui/icons-material/Add"; // Import the Add icon

const FloatingButton = ({ onClick, color = "primary", icon = <AddIcon /> }) => {
  return (
    <Fab
      color={color}
      aria-label="add"
      onClick={onClick}
      sx={{
        position: "fixed",
        bottom: 16,
        right: 16,
      }}
    >
      {icon}
    </Fab>
  );
};

export default FloatingButton;