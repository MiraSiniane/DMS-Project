import { useState, useEffect } from "react";
import { Box } from "@mui/material";
import { useNavigate } from "react-router-dom";
import SearchBar from "../material-kit/searchbar/SearchBar";
import FilesTable from "../material-kit/tables/FilesTable";
import Pagination from "../material-kit/pagination/Pagination";
import Filters from "../material-kit/filters/Filters";
import AddFileFolder from "./AddFileFolder";
import FileDetailView from "./FileDetailView";
import FilePreview from "./FilePreview";
import { getFiles, deleteFile, getFileDetails } from "../../../__api__/filesApi";
import { filterByDate } from "./utils";

const Files = ({ folderId = null }) => {
  const [searchTerm, setSearchTerm] = useState("");
  const [orderBy, setOrderBy] = useState("name");
  const [order, setOrder] = useState("asc");
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(5);
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [typeFilter, setTypeFilter] = useState([]);
  const [dateFilter, setDateFilter] = useState("");
  const [selectedFile, setSelectedFile] = useState(null);
  const [detailsOpen, setDetailsOpen] = useState(false);
  const [previewOpen, setPreviewOpen] = useState(false);
  const navigate = useNavigate();

  // Fetch files from the backend
  useEffect(() => {
    const fetchFiles = async () => {
      try {
        setLoading(true);
        const data = await getFiles(folderId);
        setFiles(data);
        setError(null);
      } catch (err) {
        setError("Failed to fetch files. Please try again.");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchFiles();
  }, [folderId]);

  // Define filter configurations for Files
  const filtersConfig = [
    {
      key: "type",
      label: "Type",
      value: typeFilter,
      multiple: true,
      options: [
        { value: "folder", label: "Folder" },
        { value: "PDF", label: "PDF" },
        { value: "PNG", label: "PNG" },
        { value: "Excel", label: "Excel" },
        { value: "Word", label: "Word" },
      ],
    },
    {
      key: "date",
      label: "Date",
      value: dateFilter,
      multiple: false,
      options: [
        { value: "last1h", label: "Last 1 Hour" },
        { value: "last24h", label: "Last 24 Hours" },
        { value: "lastWeek", label: "Last Week" },
        { value: "lastMonth", label: "Last Month" },
        { value: "last3Months", label: "Last 3 Months" },
        { value: "lastYear", label: "Last Year" },
      ],
    },
  ];

  // Handle filter changes
  const handleFilterChange = (key, value) => {
    switch (key) {
      case "type":
        setTypeFilter(value);
        break;
      case "date":
        setDateFilter(value);
        break;
      default:
        break;
    }
    setPage(0); // Reset pagination when filters change
  };

  // Handle search input
  const handleSearch = (event) => {
    setSearchTerm(event.target.value);
    setPage(0);
  };

  // Handle sorting
  const handleSort = (property) => {
    const isAsc = orderBy === property && order === "asc";
    setOrder(isAsc ? "desc" : "asc");
    setOrderBy(property);
  };

  // Handle pagination
  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  // Handle deleting a file or folder
  const handleDelete = async (item) => {
    try {
      await deleteFile(item._id);
      // Refresh the file list after deletion
      const updatedFiles = await getFiles(folderId);
      setFiles(updatedFiles);
    } catch (err) {
      setError("Failed to delete file. Please try again.");
      console.error(err);
    }
  };

  // Clear all filters
  const handleClearFilters = () => {
    setSearchTerm("");
    setTypeFilter([]);
    setDateFilter("");
    setPage(0);
  };

  // Filter and sort files
  const filteredFiles = files
    .filter((file) => {
      const matchesSearchTerm = Object.values(file).some(
        (value) => value && value.toString().toLowerCase().includes(searchTerm.toLowerCase())
      );
      const matchesType =
        typeFilter.length === 0 || typeFilter.includes(file.type);
      const matchesDate = filterByDate(file.addedOn, dateFilter);
      return matchesSearchTerm && matchesType && matchesDate;
    })
    .sort((a, b) => {
      if (order === "asc") {
        return a[orderBy] > b[orderBy] ? 1 : -1;
      } else {
        return a[orderBy] < b[orderBy] ? 1 : -1;
      }
    });

  // Paginate files
  const paginatedFiles = filteredFiles.slice(
    page * rowsPerPage,
    page * rowsPerPage + rowsPerPage
  );

  // Handle view actions - show file/folder details
  const handleView = async (item) => {
    try {
      const fileDetails = await getFileDetails(item._id);
      setSelectedFile(fileDetails);
      setDetailsOpen(true);
    } catch (err) {
      setError("Failed to fetch file details. Please try again.");
      console.error(err);
    }
  };

  // Handle row click - navigate to folder or show file preview
  const handleRowClick = (item) => {
    if (item.type === "folder") {
      navigate(`/files/folder/${item._id}`); // Navigate to the folder
    } else {
      // For files, show preview
      handleFilePreview(item);
    }
  };

  // Handle file preview
  const handleFilePreview = async (item) => {
    try {
      const fileDetails = await getFileDetails(item._id);
      setSelectedFile(fileDetails);
      setPreviewOpen(true);
    } catch (err) {
      setError("Failed to fetch file details. Please try again.");
      console.error(err);
    }
  };

  // Handle download actions
  const handleDownload = (file) => {
    // Create a download link for the file
    window.open(`http://localhost:5000/api/files/download/${file._id}`, '_blank');
  };

  if (loading) return <Box>Loading...</Box>;
  if (error) return <Box>{error}</Box>;

  return (
    <Box width="100%" overflow="auto" maxWidth="1200px" px={3}>
      {/* Search and Filters */}
      <Filters
        searchTerm={searchTerm}
        onSearch={handleSearch}
        filtersConfig={filtersConfig}
        onFilterChange={handleFilterChange}
        onClearFilters={handleClearFilters}
      />

      {/* Files Table */}
      <FilesTable
        files={paginatedFiles}
        orderBy={orderBy}
        order={order}
        onSort={handleSort}
        onView={handleView}
        onDownload={handleDownload}
        onDelete={handleDelete}
        onRowClick={handleRowClick}
      />

      {/* Pagination */}
      <Pagination
        page={page}
        rowsPerPage={rowsPerPage}
        count={filteredFiles.length}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage}
      />

      {/* Add File/Folder */}
      <AddFileFolder
        folderId={folderId}
        onFileAdded={async () => {
          const updatedFiles = await getFiles(folderId);
          setFiles(updatedFiles);
        }}
      />

      {/* File Details Dialog */}
      <FileDetailView 
        file={selectedFile} 
        open={detailsOpen} 
        onClose={() => setDetailsOpen(false)} 
      />

      {/* File Preview Dialog */}
      <FilePreview
        file={selectedFile}
        open={previewOpen}
        onClose={() => setPreviewOpen(false)}
      />
    </Box>
  );
};

export default Files;