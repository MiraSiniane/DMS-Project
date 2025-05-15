import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { 
  Box, 
  Typography, 
  Button,
  Divider,
  CircularProgress,
  Alert,
  styled
} from "@mui/material";
import axios from "axios";
import jsPDF from "jspdf";
import { format } from "date-fns";

// Styled component for status dot
const StatusDot = styled('span')(({ status }) => ({
  display: 'inline-block',
  width: 10,
  height: 10,
  borderRadius: '50%',
  backgroundColor: status === 'active' ? '#4caf50' : '#9e9e9e', // Green for active, grey for inactive
  marginRight: 8,
  verticalAlign: 'middle'
}));

const UserDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Fetch user details
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await axios.get(`http://localhost:5000/api/users/${id}`);
        setUser(response.data);
      } catch (err) {
        console.error("Failed to fetch user:", err);
        setError(err.response?.data?.message || "Failed to load user details");
      } finally {
        setLoading(false);
      }
    };
    fetchUser();
  }, [id]);

  // Export to PDF function
  const exportToPDF = () => {
    if (!user) return;

    const doc = new jsPDF();
    const margin = 10;
    let yPos = 20;

    // Title
    doc.setFontSize(18);
    doc.text("User Details Report", 105, yPos, { align: "center" });
    yPos += 10;

    // Report date
    doc.setFontSize(10);
    doc.text(`Generated on: ${format(new Date(), "PPpp")}`, 105, yPos, { align: "center" });
    yPos += 15;

    // User details
    doc.setFontSize(12);
    doc.setFont("helvetica", "bold");
    doc.text("Basic Information:", margin, yPos);
    doc.setFont("helvetica", "normal");
    yPos += 8;
    doc.text(`Name: ${user.name}`, margin, yPos);
    yPos += 8;
    doc.text(`Email: ${user.email}`, margin, yPos);
    yPos += 8;
    doc.text(`Position: ${user.position}`, margin, yPos);
    yPos += 8;
    doc.text(`Role: ${user.role}`, margin, yPos);
    yPos += 8;
    doc.text(`Department: ${user.department}`, margin, yPos);
    yPos += 8;
    doc.text(`Status: ${user.status}`, margin, yPos);
    yPos += 15;

    // Additional Information
    doc.setFont("helvetica", "bold");
    doc.text("Additional Information:", margin, yPos);
    doc.setFont("helvetica", "normal");
    yPos += 8;
    doc.text(`Last Login: ${user.lastLogin ? format(new Date(user.lastLogin), "PPpp") : "Never logged in"}`, margin, yPos);
    yPos += 8;
    doc.text(`Documents Loaded: ${user.docsLoaded || 0}`, margin, yPos);
    yPos += 8;
    doc.text(`Address: ${user.address || "Not specified"}`, margin, yPos);
    yPos += 8;
    doc.text(`Phone: ${user.phone || "Not specified"}`, margin, yPos);

    // Save the PDF
    doc.save(`user_details_${user.name.replace(/\s+/g, '_')}.pdf`);
  };

  if (loading) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", mt: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
        <Button variant="contained" onClick={() => navigate("/users")}>
          Back to Users
        </Button>
      </Box>
    );
  }

  if (!user) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography variant="h6" sx={{ mb: 2 }}>
          User not found
        </Typography>
        <Button variant="contained" onClick={() => navigate("/users")}>
          Back to Users
        </Button>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3, maxWidth: 800, mx: "auto" }}>
      <Box sx={{ display: "flex", justifyContent: "space-between", mb: 3 }}>
        <Typography variant="h4">User Details</Typography>
        <Box>
          <Button
            variant="outlined"
            onClick={() => navigate(`/users/edit/${user._id}`)}
            sx={{ mr: 2 }}
          >
            Edit User
          </Button>
          <Button variant="contained" onClick={exportToPDF}>
            Export to PDF
          </Button>
        </Box>
      </Box>

      {/* Basic Information Section */}
      <Box sx={{ mb: 4, p: 3, border: "1px solid #eee", borderRadius: 1 }}>
        <Typography variant="h6" sx={{ mb: 3 }}>
          Basic Information
        </Typography>
        
        <Box sx={{ display: "flex", flexWrap: "wrap", gap: 3 }}>
          <Box sx={{ flex: "1 1 300px" }}>
            <Typography variant="subtitle2">Full Name</Typography>
            <Typography sx={{ mb: 2 }}>{user.name}</Typography>
          </Box>
          
          <Box sx={{ flex: "1 1 300px" }}>
            <Typography variant="subtitle2">Email</Typography>
            <Typography sx={{ mb: 2 }}>{user.email}</Typography>
          </Box>
          
          <Box sx={{ flex: "1 1 300px" }}>
            <Typography variant="subtitle2">Position</Typography>
            <Typography sx={{ mb: 2 }}>{user.position}</Typography>
          </Box>
          
          <Box sx={{ flex: "1 1 300px" }}>
            <Typography variant="subtitle2">Role</Typography>
            <Typography sx={{ mb: 2 }}>{user.role}</Typography>
          </Box>
          
          <Box sx={{ flex: "1 1 300px" }}>
            <Typography variant="subtitle2">Department</Typography>
            <Typography sx={{ mb: 2 }}>{user.department}</Typography>
          </Box>
          
          <Box sx={{ flex: "1 1 300px" }}>
            <Typography variant="subtitle2">Status</Typography>
            <Box sx={{ display: "flex", alignItems: "center" }}>
              <StatusDot status={user.status} />
              <Typography sx={{ textTransform: "capitalize" }}>
                {user.status}
              </Typography>
            </Box>
          </Box>
        </Box>
      </Box>

      {/* Additional Information Section */}
      <Box sx={{ mb: 4, p: 3, border: "1px solid #eee", borderRadius: 1 }}>
        <Typography variant="h6" sx={{ mb: 3 }}>
          Additional Information
        </Typography>
        
        <Box sx={{ display: "flex", flexWrap: "wrap", gap: 3 }}>
          <Box sx={{ flex: "1 1 300px" }}>
            <Typography variant="subtitle2">Last Login</Typography>
            <Typography sx={{ mb: 2 }}>
              {user.lastLogin ? format(new Date(user.lastLogin), "PPpp") : "Never logged in"}
            </Typography>
          </Box>
          
          <Box sx={{ flex: "1 1 300px" }}>
            <Typography variant="subtitle2">Documents Loaded</Typography>
            <Typography sx={{ mb: 2 }}>{user.docsLoaded || 0}</Typography>
          </Box>
          
          <Box sx={{ flex: "1 1 300px" }}>
            <Typography variant="subtitle2">Address</Typography>
            <Typography sx={{ mb: 2 }}>{user.address || "Not specified"}</Typography>
          </Box>
          
          <Box sx={{ flex: "1 1 300px" }}>
            <Typography variant="subtitle2">Phone</Typography>
            <Typography sx={{ mb: 2 }}>{user.phone || "Not specified"}</Typography>
          </Box>
        </Box>
      </Box>

      <Divider sx={{ my: 3 }} />

      <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
        <Button variant="contained" onClick={() => navigate("/users")}>
          Back to Users List
        </Button>
      </Box>
    </Box>
  );
};

export default UserDetails;