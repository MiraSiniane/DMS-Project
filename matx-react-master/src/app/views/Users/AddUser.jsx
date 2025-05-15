import { useState } from "react";
import { 
  Box, 
  TextField, 
  Button, 
  Typography,
  MenuItem,
  FormControl,
  InputLabel,
  Select,
  FormHelperText 
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { createUser } from "../../../__api__/usersApi";

const AddUser = () => {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    position: "",
    role: "user",
    department: "IT",
    address: "",
    phone: "",
    status: "inactive" // Set default status to inactive
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  // Departments available for selection
  const departments = [
    "HR",
    "IT",
    "Finance",
    "Marketing",
    "Operations",
    "Sales"
  ];

  // Roles available for selection
  const roles = [
    { value: "admin", label: "Admin" },
    { value: "editor", label: "Editor" },
    { value: "viewer", label: "Viewer" }
  ];

  // Positions available for selection
  const positions = [
    "Manager",
    "Frontend Developer",
    "Backend Developer",
    "Fullstack Developer",
    "UI/UX Designer",
    "Product Manager",
    "QA Engineer"
  ];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: null
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.name.trim()) newErrors.name = "Name is required";
    if (!formData.email.trim()) {
      newErrors.email = "Email is required";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = "Invalid email format";
    }
    if (!formData.password) {
      newErrors.password = "Password is required";
    } else if (formData.password.length < 6) {
      newErrors.password = "Password must be at least 6 characters";
    }
    if (!formData.position) newErrors.position = "Position is required";
    if (!formData.department) newErrors.department = "Department is required";
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
    
    setIsSubmitting(true);
    
    try {
      await createUser({
        ...formData,
        docsLoaded: 0,
        lastLogin: null
      });
      navigate("/users", { state: { success: "User created successfully!" } });
    } catch (err) {
      console.error("Failed to add user:", err);
      // Error handling remains the same
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Box sx={{ p: 3, maxWidth: 800, mx: "auto" }}>
      <Typography variant="h4" sx={{ mb: 4 }}>
        Add New User
      </Typography>
      
      <form onSubmit={handleSubmit}>
        {/* Name Field */}
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
        
        {/* Email Field */}
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
        
        {/* Password Field */}
        <TextField
          fullWidth
          label="Password"
          name="password"
          type="password"
          value={formData.password}
          onChange={handleChange}
          error={!!errors.password}
          helperText={errors.password || "Minimum 6 characters"}
          sx={{ mb: 3 }}
          required
        />
        
        {/* Position Field */}
        <FormControl fullWidth sx={{ mb: 3 }} error={!!errors.position}>
          <InputLabel>Position *</InputLabel>
          <Select
            name="position"
            value={formData.position}
            label="Position *"
            onChange={handleChange}
            required
          >
            {positions.map((position) => (
              <MenuItem key={position} value={position}>
                {position}
              </MenuItem>
            ))}
          </Select>
          {errors.position && <FormHelperText>{errors.position}</FormHelperText>}
        </FormControl>
        
        {/* Role Field */}
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
        
        {/* Department Field */}
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
              <MenuItem key={dept} value={dept}>
                {dept}
              </MenuItem>
            ))}
          </Select>
          {errors.department && <FormHelperText>{errors.department}</FormHelperText>}
        </FormControl>
        
        {/* Address Field */}
        <TextField
          fullWidth
          label="Address"
          name="address"
          value={formData.address}
          onChange={handleChange}
          sx={{ mb: 3 }}
        />
        
        {/* Phone Field */}
        <TextField
          fullWidth
          label="Phone Number"
          name="phone"
          value={formData.phone}
          onChange={handleChange}
          sx={{ mb: 3 }}
        />
        
        {/* Hidden Status Field (inactive by default) */}
        <input type="hidden" name="status" value="inactive" />
        
        {/* Action Buttons */}
        <Box sx={{ display: "flex", justifyContent: "flex-end", gap: 2, mt: 4 }}>
          <Button
            type="button"
            variant="outlined"
            onClick={() => navigate("/users")}
            disabled={isSubmitting}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            variant="contained"
            color="primary"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Creating User..." : "Create User"}
          </Button>
        </Box>
        
        {/* Error Message */}
        {errors.submit && (
          <Typography color="error" sx={{ mt: 3, textAlign: "center" }}>
            {errors.submit}
          </Typography>
        )}
      </form>
    </Box>
  );
};

export default AddUser;