import {
  Table,
  TableRow,
  TableBody,
  TableCell,
  TableHead,
  IconButton,
  TableSortLabel,
  Box,
  Tooltip,
} from "@mui/material";
import VisibilityIcon from "@mui/icons-material/Visibility";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

const UserTable = ({ users = [], orderBy, order, onSort, onEdit, onDelete, onView }) => {
  // Function to truncate text
  const truncateText = (text, maxLength) => {
    if (!text) return ""; // Handle undefined or null
    if (text.length > maxLength) {
      return text.substring(0, maxLength) + "...";
    }
    return text;
  };

  return (
    <Table>
      <TableHead>
        <TableRow>
          <TableCell align="left" width="5%">
            <TableSortLabel
              active={orderBy === "_id"}
              direction={orderBy === "_id" ? order : "asc"}
              onClick={() => onSort("_id")}
            >
              ID
            </TableSortLabel>
          </TableCell>
          <TableCell align="left" width="15%">
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
              active={orderBy === "email"}
              direction={orderBy === "email" ? order : "asc"}
              onClick={() => onSort("email")}
            >
              Email
            </TableSortLabel>
          </TableCell>
          <TableCell align="left" width="15%">
            <TableSortLabel
              active={orderBy === "position"}
              direction={orderBy === "position" ? order : "asc"}
              onClick={() => onSort("position")}
            >
              Position
            </TableSortLabel>
          </TableCell>
          <TableCell align="left" width="10%">
            <TableSortLabel
              active={orderBy === "role"}
              direction={orderBy === "role" ? order : "asc"}
              onClick={() => onSort("role")}
            >
              Role
            </TableSortLabel>
          </TableCell>
          <TableCell align="left" width="15%">
            <TableSortLabel
              active={orderBy === "department"}
              direction={orderBy === "department" ? order : "asc"}
              onClick={() => onSort("department")}
            >
              Department
            </TableSortLabel>
          </TableCell>
          <TableCell align="left" width="5%">
            Status
          </TableCell>
          <TableCell align="center" width="15%">
            Actions
          </TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {users.map((user, index) => (
          <TableRow key={index}>
            <TableCell align="left">{user._id}</TableCell> {/* Use _id here */}
            <TableCell align="left">
              <Tooltip title={user.name} placement="top">
                <Box
                  sx={{
                    whiteSpace: "nowrap",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                    maxWidth: "150px",
                  }}
                >
                  {truncateText(user.name, 20)}
                </Box>
              </Tooltip>
            </TableCell>
            <TableCell align="left">
              <Tooltip title={user.email} placement="top">
                <Box
                  sx={{
                    whiteSpace: "nowrap",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                    maxWidth: "150px",
                  }}
                >
                  {truncateText(user.email, 20)}
                </Box>
              </Tooltip>
            </TableCell>
            <TableCell align="left">
              <Tooltip title={user.position} placement="top">
                <Box
                  sx={{
                    whiteSpace: "nowrap",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                    maxWidth: "150px",
                  }}
                >
                  {truncateText(user.position, 20)}
                </Box>
              </Tooltip>
            </TableCell>
            <TableCell align="left">
              <Tooltip title={user.role} placement="top">
                <Box
                  sx={{
                    whiteSpace: "nowrap",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                    maxWidth: "100px",
                  }}
                >
                  {truncateText(user.role, 15)}
                </Box>
              </Tooltip>
            </TableCell>
            <TableCell align="left">
              <Tooltip title={user.department} placement="top">
                <Box
                  sx={{
                    whiteSpace: "nowrap",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                    maxWidth: "150px",
                  }}
                >
                  {truncateText(user.department, 20)}
                </Box>
              </Tooltip>
            </TableCell>
            <TableCell align="left">
              <Box
                sx={{
                  width: 10,
                  height: 10,
                  borderRadius: "50%",
                  backgroundColor: user.status === "active" ? "green" : "grey",
                }}
              />
            </TableCell>
            <TableCell align="right">
              {/* View Icon */}
              <IconButton onClick={() => onView(user)}>
                <VisibilityIcon color="info" />
              </IconButton>
              {/* Edit Icon */}
              <IconButton onClick={() => onEdit(user)}>
                <EditIcon color="primary" />
              </IconButton>
              {/* Delete Icon */}
              <IconButton onClick={() => onDelete(user)}>
                <DeleteIcon color="error" />
              </IconButton>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
};

export default UserTable;