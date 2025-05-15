import {
  Table,
  TableRow,
  TableBody,
  TableCell,
  TableHead,
  IconButton,
  TableSortLabel,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Button,
} from "@mui/material";
import VisibilityIcon from "@mui/icons-material/Visibility";
import GetAppIcon from "@mui/icons-material/GetApp";
import DeleteIcon from "@mui/icons-material/Delete";
import FolderIcon from '@mui/icons-material/Folder';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import ImageIcon from '@mui/icons-material/Image';
import DescriptionIcon from '@mui/icons-material/Description';
import TableChartIcon from '@mui/icons-material/TableChart';
import { useState } from "react";

// Add a mapping component
const FileTypeIcon = ({ type }) => {
  switch (type.toLowerCase()) {
    case 'folder':
      return <FolderIcon color="primary" />;
    case 'pdf':
      return <PictureAsPdfIcon color="error" />;
    case 'png':
    case 'jpg':
    case 'jpeg':
    case 'gif':
      return <ImageIcon color="primary" />;
    case 'xlsx':
    case 'csv':
      return <TableChartIcon color="success" />;
    case 'doc':
    case 'docx':
      return <DescriptionIcon color="info" />;
    default:
      return <DescriptionIcon />;
  }
};

// Helper function to get file extension
const getFileExtension = (filename) => {
  if (!filename) return '';
  return filename.split('.').pop().toUpperCase();
};

const FilesTable = ({ files, orderBy, order, onSort, onView, onDownload, onDelete, onRowClick }) => {
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [deletePassword, setDeletePassword] = useState('');
  const [itemToDelete, setItemToDelete] = useState(null);
  const [passwordError, setPasswordError] = useState(false);

  const handleDeleteClick = (file, event) => {
    event.stopPropagation(); // Prevent row click event
    setItemToDelete(file);
    setDeletePassword('');
    setPasswordError(false);
    setShowDeleteDialog(true);
  };

  const confirmDelete = () => {
    if (deletePassword === 'adminpassword') {
      onDelete(itemToDelete);
      setShowDeleteDialog(false);
      setPasswordError(false);
    } else {
      setPasswordError(true);
    }
  };

  const handleViewClick = (file, event) => {
    event.stopPropagation(); // Prevent row click event
    onView(file);
  };

  const handleDownloadClick = (file, event) => {
    event.stopPropagation(); // Prevent row click event
    onDownload(file);
  };

  const handleRowClick = (file) => {
    // Simply call the parent function and let it handle navigation/preview logic
    onRowClick(file);
  };

  return (
    <>
      <Table>
        <TableHead>
          <TableRow>
            {/* Type column without title */}
            <TableCell width="5%"></TableCell>
            <TableCell align="left" width="30%">
              <TableSortLabel
                active={orderBy === "name"}
                direction={orderBy === "name" ? order : "asc"}
                onClick={() => onSort("name")}
              >
                Name
              </TableSortLabel>
            </TableCell>
            <TableCell align="left" width="15%">
              <TableSortLabel
                active={orderBy === "type"}
                direction={orderBy === "type" ? order : "asc"}
                onClick={() => onSort("type")}
              >
                Type
              </TableSortLabel>
            </TableCell>
            <TableCell align="left" width="20%">
              <TableSortLabel
                active={orderBy === "addedOn"}
                direction={orderBy === "addedOn" ? order : "asc"}
                onClick={() => onSort("addedOn")}
              >
                Added On
              </TableSortLabel>
            </TableCell>
            <TableCell align="left" width="15%">
              <TableSortLabel
                active={orderBy === "size"}
                direction={orderBy === "size" ? order : "asc"}
                onClick={() => onSort("size")}
              >
                Size
              </TableSortLabel>
            </TableCell>
            <TableCell align="right" width="15%">
              Actions
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {files.map((file, index) => (
            <TableRow 
              key={index} 
              onClick={() => handleRowClick(file)}
              hover
              style={{ cursor: 'pointer' }}
            >
              {/* Icon column */}
              <TableCell>
                <FileTypeIcon type={file.type} />
              </TableCell>
              <TableCell align="left">{file.name}</TableCell>
              <TableCell align="left">
                {file.type === 'folder' ? 'Folder' : getFileExtension(file.name)}
              </TableCell>
              <TableCell align="left">{new Date(file.addedOn).toLocaleString()}</TableCell>
              <TableCell align="left">
                {file.type === 'folder' ? '-' : `${Math.round(file.size / 1024)} KB`}
              </TableCell>
              <TableCell align="right">
                {/* View Icon */}
                <IconButton onClick={(e) => handleViewClick(file, e)}>
                  <VisibilityIcon color="info" />
                </IconButton>
                {/* Download Icon */}
                <IconButton onClick={(e) => handleDownloadClick(file, e)}>
                  <GetAppIcon color="primary" />
                </IconButton>
                {/* Delete Icon */}
                <IconButton onClick={(e) => handleDeleteClick(file, e)}>
                  <DeleteIcon color="error" />
                </IconButton>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      {/* Delete Confirmation Dialog with Password */}
      <Dialog open={showDeleteDialog} onClose={() => setShowDeleteDialog(false)}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete {itemToDelete?.name}? Please enter admin password to confirm.
          </DialogContentText>
          <input
            type="password"
            placeholder="Enter password"
            value={deletePassword}
            onChange={(e) => setDeletePassword(e.target.value)}
            style={{ 
              width: '100%', 
              padding: '10px', 
              marginTop: '20px',
              border: passwordError ? '1px solid red' : '1px solid #ccc',
              borderRadius: '4px'
            }}
          />
          {passwordError && (
            <p style={{ color: 'red', margin: '5px 0 0' }}>Incorrect password</p>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setShowDeleteDialog(false)}>Cancel</Button>
          <Button onClick={confirmDelete} color="error">Delete</Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default FilesTable;