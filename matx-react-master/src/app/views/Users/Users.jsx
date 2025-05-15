import axios from "axios";
import { useState, useEffect } from "react";
import { 
  Box,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  DialogContentText
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import SearchBar from "../material-kit/searchbar/SearchBar";
import UserTable from "../material-kit/tables/UsersTable";
import Pagination from "../material-kit/pagination/Pagination";
import Filters from "../material-kit/filters/Filters";
import FloatingButton from "../../components/FloatingButton";
import { getUsers, deleteUser, checkAdminPassword } from "../../../__api__/usersApi";

const Users = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [orderBy, setOrderBy] = useState("name");
  const [order, setOrder] = useState("asc");
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(5);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [roleFilter, setRoleFilter] = useState([]);
  const [statusFilter, setStatusFilter] = useState([]);
  const [positionFilter, setPositionFilter] = useState([]);
  const [departmentFilter, setDepartmentFilter] = useState([]);
  const navigate = useNavigate();

  // Delete confirmation dialog state
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [userToDelete, setUserToDelete] = useState(null);
  const [confirmationPassword, setConfirmationPassword] = useState("");
  const [deleteError, setDeleteError] = useState("");

  // Fetch users from the backend
  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const usersData = await getUsers();
        setUsers(usersData);
      } catch (err) {
        console.error("Failed to fetch users", err);
      } finally {
        setLoading(false);
      }
    };
    fetchUsers();
  }, []);

  // Define filter configurations for Users
  const filtersConfig = [
    {
      key: "role",
      label: "Role",
      value: roleFilter,
      multiple: true,
      options: [
        { value: "admin", label: "Admin" },
        { value: "manager", label: "Manager" },
        { value: "user", label: "User" },
      ],
    },
    {
      key: "status",
      label: "Status",
      value: statusFilter,
      multiple: true,
      options: [
        { value: "active", label: "Active" },
        { value: "inactive", label: "Inactive" },
      ],
    },
    {
      key: "position",
      label: "Position",
      value: positionFilter,
      multiple: true,
      options: [
        { value: "Manager", label: "Manager" },
        { value: "Developer", label: "Developer" },
        { value: "Designer", label: "Designer" },
        { value: "Analyst", label: "Analyst" },
      ],
    },
    {
      key: "department",
      label: "Department",
      value: departmentFilter,
      multiple: true,
      options: [
        { value: "HR", label: "HR" },
        { value: "IT", label: "IT" },
        { value: "Finance", label: "Finance" },
        { value: "Marketing", label: "Marketing" },
        { value: "Operations", label: "Operations" },
      ],
    },
  ];

  // Handle filter changes
  const handleFilterChange = (key, value) => {
    switch (key) {
      case "role":
        setRoleFilter(value);
        break;
      case "status":
        setStatusFilter(value);
        break;
      case "position":
        setPositionFilter(value);
        break;
      case "department":
        setDepartmentFilter(value);
        break;
      default:
        break;
    }
    setPage(0);
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

  // Clear all filters
  const handleClearFilters = () => {
    setSearchTerm("");
    setRoleFilter([]);
    setStatusFilter([]);
    setPositionFilter([]);
    setDepartmentFilter([]);
    setPage(0);
  };

  // Filter users based on search term and filters
  const filteredUsers = users
    .filter((user) => {
      const matchesSearchTerm = Object.values(user).some(
        (value) =>
          value && value.toString().toLowerCase().includes(searchTerm.toLowerCase())
      );
      const matchesRole = roleFilter.length === 0 || roleFilter.includes(user.role);
      const matchesStatus = statusFilter.length === 0 || statusFilter.includes(user.status);
      const matchesPosition = positionFilter.length === 0 || positionFilter.includes(user.position);
      const matchesDepartment = departmentFilter.length === 0 || departmentFilter.includes(user.department);
      
      return matchesSearchTerm && matchesRole && matchesStatus && matchesPosition && matchesDepartment;
    })
    .sort((a, b) => {
      if (order === "asc") {
        return a[orderBy] > b[orderBy] ? 1 : -1;
      } else {
        return a[orderBy] < b[orderBy] ? 1 : -1;
      }
    });

  // Paginate users
  const paginatedUsers = filteredUsers.slice(
    page * rowsPerPage,
    page * rowsPerPage + rowsPerPage
  );

  // Handle view actions
  const handleView = (user) => {
    navigate(`/users/${user._id}`);
  };

  // Handle editing a user
  const handleEdit = (user) => {
    navigate(`/users/edit/${user._id}`);
  };

  // Handle delete confirmation dialog
  const handleDeleteConfirmation = (user) => {
    setUserToDelete(user);
    setDeleteDialogOpen(true);
    setConfirmationPassword("");
    setDeleteError("");
  };

  // Perform the actual deletion
  const confirmDelete = async () => {
    try {
      const isAdmin = await checkAdminPassword(confirmationPassword);
      if (!isAdmin) {
        setDeleteError("Incorrect confirmation password");
        return;
      }
  
      await deleteUser(userToDelete._id);
      setUsers(users.filter((u) => u._id !== userToDelete._id));
      setDeleteDialogOpen(false);
    } catch (err) {
      console.error("Failed to delete user", err);
      setDeleteError(err.message || "Failed to delete user");
    }
  };

  if (loading) {
    return <Box>Loading users...</Box>;
  }

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

      {/* User Table */}
      <UserTable
        users={paginatedUsers}
        orderBy={orderBy}
        order={order}
        onSort={handleSort}
        onView={handleView}
        onEdit={handleEdit}
        onDelete={handleDeleteConfirmation}
      />

      {/* Pagination */}
      <Pagination
        page={page}
        rowsPerPage={rowsPerPage}
        count={filteredUsers.length}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage}
      />

      {/* Floating Button for Adding Users */}
      <FloatingButton onClick={() => navigate("/users/add")} />

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
        <DialogTitle>Confirm User Deletion</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete {userToDelete?.name}? Please enter admin password to confirm.
          </DialogContentText>
          <input
            type="password"
            placeholder="Enter password"
            value={confirmationPassword}
            onChange={(e) => setConfirmationPassword(e.target.value)}
            error={!!deleteError}
            helperText={deleteError}
            style={{ 
              width: '100%', 
              padding: '10px', 
              marginTop: '20px',
              border: deleteError ? '1px solid red' : '1px solid #ccc',
              borderRadius: '4px'
            }}
          />
          {deleteError && (
            <p style={{ color: 'red', margin: '5px 0 0' }}>Incorrect password</p>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
          <Button onClick={confirmDelete} color="error"> Delete </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Users;