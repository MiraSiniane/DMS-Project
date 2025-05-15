import { useParams } from "react-router-dom";
import Files from "./Files"; // Reuse the Files component

const FolderContents = () => {
  const { folderId } = useParams(); // Get folder ID from URL
  
  // Pass the folder ID to the Files component
  return <Files folderId={folderId} />;
};

export default FolderContents;