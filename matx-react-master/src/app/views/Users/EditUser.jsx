import { useState, useEffect } from "react";
import { 
  Box, 
  TextField, 
  Button, 
  Typography,
  MenuItem,
  FormControl,
  InputLabel,
  Select,
  FormHelperText,
  Alert
} from "@mui/material";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { getUser, updateUser } from "../../../__api__/usersApi";

const EditUser = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [successMessage, setSuccessMessage] = useState("");

  // Form state
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    position: "",
    role: "user",
    department: "IT",
    address: "",
    phone: ""
  });

  const [errors, setErrors] = useState({});

  // Available options
  const departments = ["HR", "IT", "Finance", "Marketing", "Operations", "Sales"];
  const roles = [
    { value: "admin", label: "Admin" },
    { value: "editor", label: "Editor" },
    { value: "viewer", label: "Viewer" }
  ];
  const positions = [
    "Manager",
    "Frontend Developer",
    "Backend Developer",
    "Fullstack Developer",
    "UI/UX Designer",
    "Product Manager",
    "QA Engineer"
  ];

  // Fetch user data
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const userData = await getUser(id);
        setFormData({
          name: userData.name,
          email: userData.email,
          position: userData.position,
          role: userData.role,
          department: userData.department,
          address: userData.address || "",
          phone: userData.phone || ""
        });
      } catch (err) {
        console.error("Failed to fetch user", err);
        setErrors({ fetch: err.message || "Failed to load user data" });
      } finally {
        setLoading(false);
      }
    };
    fetchUser();
  }, [id]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: null }));
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.name.trim()) newErrors.name = "Name is required";
    if (!formData.email.trim()) {
      newErrors.email = "Email is required";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = "Invalid email format";
    }
    if (!formData.position) newErrors.position = "Position is required";
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
  
    try {
      await updateUser(id, formData);
      setSuccessMessage("User updated successfully!");
      setTimeout(() => navigate("/users"), 1500);
    } catch (err) {
      console.error("Update failed:", err);
      // Error handling remains the same
    }
  };

  if (loading) return <Typography>Loading user data...</Typography>;
  if (errors.fetch) return <Typography color="error">{errors.fetch}</Typography>;

  return (
    <Box sx={{ p: 3, maxWidth: 800, mx: "auto" }}>
      <Typography variant="h4" sx={{ mb: 3 }}>
        Edit User
      </Typography>

      {successMessage && (
        <Alert severity="success" sx={{ mb: 3 }}>
          {successMessage}
        </Alert>
      )}

      <form onSubmit={handleSubmit}>
        {/* Basic Information Section */}
        <Typography variant="h6" sx={{ mb: 2 }}>
          Basic Information
        </Typography>
        
        <TextField
          fullWidth
          label="Full Name"
          name="name"
          value={formData.name}
          onChange={handleChange}
          error={!!errors.name}
          helperText={errors.name}
          sx={{ mb: 3 }}
          required
        />
        
        <TextField
          fullWidth
          label="Email"
          name="email"
          type="email"
          value={formData.email}
          onChange={handleChange}
          error={!!errors.email}
          helperText={errors.email}
          sx={{ mb: 3 }}
          required
        />

        <FormControl fullWidth sx={{ mb: 3 }} error={!!errors.position}>
          <InputLabel>Position *</InputLabel>
          <Select
            name="position"
            value={formData.position}
            label="Position *"
            onChange={handleChange}
            required
          >
            {positions.map((pos) => (
              <MenuItem key={pos} value={pos}>{pos}</MenuItem>
            ))}
          </Select>
          {errors.position && <FormHelperText>{errors.position}</FormHelperText>}
        </FormControl>

        <FormControl fullWidth sx={{ mb: 3 }} error={!!errors.role}>
          <InputLabel>Role *</InputLabel>
          <Select
            name="role"
            value={formData.role}
            label="Role *"
            onChange={handleChange}
            required
          >
            {roles.map((role) => (
              <MenuItem key={role.value} value={role.value}>
                {role.label}
              </MenuItem>
            ))}
          </Select>
          {errors.role && <FormHelperText>{errors.role}</FormHelperText>}
        </FormControl>

        <FormControl fullWidth sx={{ mb: 3 }} error={!!errors.department}>
          <InputLabel>Department *</InputLabel>
          <Select
            name="department"
            value={formData.department}
            label="Department *"
            onChange={handleChange}
            required
          >
            {departments.map((dept) => (
              <MenuItem key={dept} value={dept}>{dept}</MenuItem>
            ))}
          </Select>
          {errors.department && <FormHelperText>{errors.department}</FormHelperText>}
        </FormControl>

        <TextField
          fullWidth
          label="Address"
          name="address"
          value={formData.address}
          onChange={handleChange}
          sx={{ mb: 3 }}
        />

        <TextField
          fullWidth
          label="Phone Number"
          name="phone"
          value={formData.phone}
          onChange={handleChange}
          sx={{ mb: 3 }}
        />

        {/* Action Buttons */}
        <Box sx={{ display: "flex", justifyContent: "flex-end", gap: 2, mt: 4 }}>
          <Button
            variant="outlined"
            onClick={() => navigate("/users")}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            variant="contained"
            color="primary"
          >
            Save Changes
          </Button>
        </Box>

        {errors.submit && (
          <Typography color="error" sx={{ mt: 2, textAlign: "center" }}>
            {errors.submit}
          </Typography>
        )}
      </form>
    </Box>
  );
};

export default EditUser;