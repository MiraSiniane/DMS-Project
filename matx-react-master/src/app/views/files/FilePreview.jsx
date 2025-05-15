import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  Typography,
  CircularProgress
} from '@mui/material';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import ImageIcon from '@mui/icons-material/Image';
import DescriptionIcon from '@mui/icons-material/Description';
import TableChartIcon from '@mui/icons-material/TableChart';

const FilePreview = ({ file, open, onClose }) => {
  if (!file) return null;

  const renderPreview = () => {
    const fileType = file.type?.toLowerCase();
    
    // For image files, display the image
    if (fileType === 'png' || file.name?.toLowerCase().endsWith('.png') || 
        fileType === 'jpg' || file.name?.toLowerCase().endsWith('.jpg') ||
        fileType === 'jpeg' || file.name?.toLowerCase().endsWith('.jpeg') ||
        fileType === 'gif' || file.name?.toLowerCase().endsWith('.gif')) {
      return (
        <Box sx={{ textAlign: 'center', p: 2 }}>
          <img 
            src={`http://localhost:5000/api/files/download/${file._id}`} 
            alt={file.name}
            style={{ maxWidth: '100%', maxHeight: '70vh' }}
            onError={(e) => {
              e.target.onerror = null;
              e.target.src = ""; // Clear the source
              e.target.alt = "Error loading image";
            }}
          />
        </Box>
      );
    }
    
    // For PDF files
    else if (fileType === 'pdf' || file.name?.toLowerCase().endsWith('.pdf')) {
      return (
        <Box sx={{ textAlign: 'center', p: 2, height: '70vh' }}>
          <PictureAsPdfIcon sx={{ fontSize: 60, color: '#f44336', mb: 2 }} />
          <Typography>
            PDF preview not available. Please download the file.
          </Typography>
        </Box>
      );
    }
    
    // For Excel files
    else if (fileType === 'excel' || 
             file.name?.toLowerCase().endsWith('.xlsx') || 
             file.name?.toLowerCase().endsWith('.xls') || 
             file.name?.toLowerCase().endsWith('.csv')) {
      return (
        <Box sx={{ textAlign: 'center', p: 2 }}>
          <TableChartIcon sx={{ fontSize: 60, color: '#4caf50', mb: 2 }} />
          <Typography>
            Spreadsheet preview not available. Please download the file.
          </Typography>
        </Box>
      );
    }
    
    // For Word documents
    else if (fileType === 'word' || 
             file.name?.toLowerCase().endsWith('.doc') || 
             file.name?.toLowerCase().endsWith('.docx')) {
      return (
        <Box sx={{ textAlign: 'center', p: 2 }}>
          <DescriptionIcon sx={{ fontSize: 60, color: '#2196f3', mb: 2 }} />
          <Typography>
            Document preview not available. Please download the file.
          </Typography>
        </Box>
      );
    }
    
    // Default for other file types
    else {
      return (
        <Box sx={{ textAlign: 'center', p: 2 }}>
          <DescriptionIcon sx={{ fontSize: 60, mb: 2 }} />
          <Typography>
            Preview not available for this file type. Please download the file.
          </Typography>
        </Box>
      );
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="lg"
      fullWidth
    >
      <DialogTitle>{file.name}</DialogTitle>
      <DialogContent dividers>
        {renderPreview()}
      </DialogContent>
      <DialogActions>
        <Button 
          href={`http://localhost:5000/api/files/download/${file._id}`} 
          target="_blank"
          color="primary"
        >
          Download
        </Button>
        <Button onClick={onClose}>Close</Button>
      </DialogActions>
    </Dialog>
  );
};

export default FilePreview;