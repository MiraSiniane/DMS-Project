import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Grid,
  Typography,
  Paper,
  Box,
  Divider
} from '@mui/material';
import FolderIcon from '@mui/icons-material/Folder';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import ImageIcon from '@mui/icons-material/Image';
import DescriptionIcon from '@mui/icons-material/Description';
import TableChartIcon from '@mui/icons-material/TableChart';

const FileDetailView = ({ file, open, onClose }) => {
  if (!file) return null;

  const getFileIcon = () => {
    switch (file.type?.toLowerCase()) {
      case 'folder':
        return <FolderIcon sx={{ fontSize: 60 }} color="primary" />;
      case 'pdf':
        return <PictureAsPdfIcon sx={{ fontSize: 60 }} color="error" />;
      case 'png':
      case 'jpg':
      case 'jpeg':
      case 'gif':
        return <ImageIcon sx={{ fontSize: 60 }} color="primary" />;
      case 'excel':
        return <TableChartIcon sx={{ fontSize: 60 }} color="success" />;
      case 'word':
        return <DescriptionIcon sx={{ fontSize: 60 }} color="info" />;
      default:
        return <DescriptionIcon sx={{ fontSize: 60 }} />;
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleString();
  };

  const formatSize = (size) => {
    if (!size || file.type === 'folder') return 'N/A';
    if (size < 1024) return `${size} B`;
    if (size < 1024 * 1024) return `${Math.round(size / 1024)} KB`;
    return `${Math.round(size / (1024 * 1024))} MB`;
  };

  return (
    <Dialog 
      open={open} 
      onClose={onClose}
      maxWidth="md"
      fullWidth
    >
      <DialogTitle>
        File Details
      </DialogTitle>
      <DialogContent>
        <Paper elevation={0} sx={{ p: 2 }}>
          <Grid container spacing={2}>
            <Grid item xs={12} md={3} sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
              <Box sx={{ textAlign: 'center' }}>
                {getFileIcon()}
                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                  {file.type}
                </Typography>
              </Box>
            </Grid>
            <Grid item xs={12} md={9}>
              <Box sx={{ mb: 2 }}>
                <Typography variant="h6" gutterBottom>{file.name}</Typography>
                <Divider sx={{ my: 1 }} />
                
                <Grid container spacing={1}>
                  <Grid item xs={4}>
                    <Typography variant="body2" color="text.secondary">Path:</Typography>
                  </Grid>
                  <Grid item xs={8}>
                    <Typography variant="body2">{file.path || 'N/A'}</Typography>
                  </Grid>
                  
                  <Grid item xs={4}>
                    <Typography variant="body2" color="text.secondary">Type:</Typography>
                  </Grid>
                  <Grid item xs={8}>
                    <Typography variant="body2">{file.type}</Typography>
                  </Grid>
                  
                  <Grid item xs={4}>
                    <Typography variant="body2" color="text.secondary">Size:</Typography>
                  </Grid>
                  <Grid item xs={8}>
                    <Typography variant="body2">{formatSize(file.size)}</Typography>
                  </Grid>
                  
                  <Grid item xs={4}>
                    <Typography variant="body2" color="text.secondary">Added By:</Typography>
                  </Grid>
                  <Grid item xs={8}>
                    <Typography variant="body2">Owner</Typography>
                  </Grid>
                  
                  <Grid item xs={4}>
                    <Typography variant="body2" color="text.secondary">Added On:</Typography>
                  </Grid>
                  <Grid item xs={8}>
                    <Typography variant="body2">{formatDate(file.addedOn)}</Typography>
                  </Grid>
                </Grid>
              </Box>
            </Grid>
          </Grid>
        </Paper>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Close</Button>
      </DialogActions>
    </Dialog>
  );
};

export default FileDetailView;