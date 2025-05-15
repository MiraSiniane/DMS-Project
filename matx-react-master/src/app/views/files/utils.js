export const filterByDate = (fileDate, dateFilter) => {
    const now = new Date();
    const fileDateObj = new Date(fileDate);
  
    switch (dateFilter) {
      case "last1h":
        return now - fileDateObj <= 3600 * 1000;
      case "last24h":
        return now - fileDateObj <= 24 * 3600 * 1000;
      case "lastWeek":
        return now - fileDateObj <= 7 * 24 * 3600 * 1000;
      case "lastMonth":
        return now - fileDateObj <= 30 * 24 * 3600 * 1000;
      case "last3Months":
        return now - fileDateObj <= 90 * 24 * 3600 * 1000;
      case "lastYear":
        return now - fileDateObj <= 365 * 24 * 3600 * 1000;
      default:
        return true;
    }
  };