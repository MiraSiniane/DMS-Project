const fileList = [
    {
      id: 1,
      name: "Folder 1",
      type: "folder",
      addedOn: "2023-10-01",
      addedBy: "User 1",
      contents: [
        {
          id: 2,
          name: "File 1.pdf",
          type: "PDF",
          addedOn: "2023-10-02",
          size: "1.2 MB",
          addedBy: "User 1",
          lastViewDate: "2023-10-03",
          lastViewBy: "User 2",
          url: "https://example.com/file1.pdf", // Ensure this is valid
        },
      ],
    },
    {
      id: 3,
      name: "File 2.png",
      type: "PNG",
      addedOn: "2023-10-03",
      size: "0.5 MB",
      addedBy: "User 2",
      lastViewDate: "2023-10-04",
      lastViewBy: "User 1",
      url: "https://example.com/file2.png",
    },
  ];
  
  export default fileList;