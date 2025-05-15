import { useState } from "react";
import { Button, Dialog, DialogTitle, DialogContent, DialogActions, TextField, List, ListItem, ListItemText, Popover } from "@mui/material";
import FloatingButton from "../../components/FloatingButton";
import InsertDriveFileIcon from '@mui/icons-material/InsertDriveFile';
import CreateNewFolderIcon from '@mui/icons-material/CreateNewFolder';
import { Folder } from "@mui/icons-material";
import { uploadFile, createFolder } from "../../../__api__/filesApi";

const AddFileFolder = ({ folderId, onFileAdded }) => {
  const [openAddDialog, setOpenAddDialog] = useState(false);
  const [newFolderName, setNewFolderName] = useState("");
  const [anchorEl, setAnchorEl] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleFloatingButtonClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleOptionsClose = () => {
    setAnchorEl(null);
  };

  const handleFileOptionClick = () => {
    document.getElementById("fileInput").click();
    handleOptionsClose();
  };

  const handleFolderOptionClick = () => {
    setOpenAddDialog(true);
    handleOptionsClose();
  };

  const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (file) {
      try {
        setLoading(true);
        setError(null);
        await uploadFile(file, folderId);
        // Notify parent component to refresh file list
        if (onFileAdded) onFileAdded();
      } catch (err) {
        setError("Failed to upload file. Please try again.");
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
  };

  const handleAddFolder = async () => {
    if (newFolderName.trim()) {
      try {
        setLoading(true);
        setError(null);
        await createFolder(newFolderName, folderId);
        // Notify parent component to refresh file list
        if (onFileAdded) onFileAdded();
        setNewFolderName("");
        setOpenAddDialog(false);
      } catch (err) {
        setError("Failed to create folder. Please try again.");
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
  };

  return (
    <>
      <FloatingButton onClick={handleFloatingButtonClick} disabled={loading}>
        Add
      </FloatingButton>

      {error && <div style={{ color: 'red', marginTop: '10px' }}>{error}</div>}

      <Popover
        open={Boolean(anchorEl)}
        anchorEl={anchorEl}
        onClose={handleOptionsClose}
        anchorOrigin={{
          vertical: "top",
          horizontal: "right",
        }}
        transformOrigin={{
          vertical: "bottom",
          horizontal: "right",
        }}
      >
        <List>
          <ListItem button onClick={handleFileOptionClick}>
            <ListItem>
              <InsertDriveFileIcon />
            </ListItem>
            <ListItemText primary="File" />
          </ListItem>
          <ListItem button onClick={handleFolderOptionClick}>
            <ListItem>
              <Folder/>
            </ListItem>
            <ListItemText primary="Folder" />
          </ListItem>
        </List>
      </Popover>

      <Dialog open={openAddDialog} onClose={() => setOpenAddDialog(false)}>
        <DialogTitle>Add Folder</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="Folder Name"
            value={newFolderName}
            onChange={(e) => setNewFolderName(e.target.value)}
            sx={{ mb: 2 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenAddDialog(false)} disabled={loading}>Cancel</Button>
          <Button 
            onClick={handleAddFolder} 
            color="primary"
            disabled={loading || !newFolderName.trim()}
          >
            {loading ? "Creating..." : "OK"}
          </Button>
        </DialogActions>
      </Dialog>

      <input
        id="fileInput"
        type="file"
        style={{ display: "none" }}
        onChange={handleFileUpload}
      />
    </>
  );
};

export default AddFileFolder;