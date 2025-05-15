// app/components/Pagination.jsx
import { TablePagination } from "@mui/material";

const Pagination = ({ page, rowsPerPage, count, onPageChange, onRowsPerPageChange }) => {
  return (
    <TablePagination
      rowsPerPageOptions={[5, 10, 25]}
      component="div"
      count={count}
      rowsPerPage={rowsPerPage}
      page={page}
      onPageChange={onPageChange}
      onRowsPerPageChange={onRowsPerPageChange}
    />
  );
};

export default Pagination;