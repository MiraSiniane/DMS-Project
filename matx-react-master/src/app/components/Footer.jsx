import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import { ThemeProvider, styled, useTheme } from "@mui/material/styles";

import useSettings from "app/hooks/useSettings";
import { topBarHeight } from "app/utils/constant";

// STYLED COMPONENTS
const AppFooter = styled(Toolbar)(() => ({
  display: "flex",
  alignItems: "center",
  minHeight: topBarHeight,
  "@media (max-width: 499px)": {
    display: "table",
    width: "100%",
    minHeight: "auto",
    padding: "1rem 0",
    "& .container": {
      flexDirection: "column !important",
      "& a": { margin: "0 0 16px !important" }
    }
  }
}));

export default function Footer() {
  const theme = useTheme();
  const { settings } = useSettings();

  const footerTheme = settings.themes[settings.footer.theme] || theme;

  return (
    <ThemeProvider theme={footerTheme}>
      <AppBar color="primary" position="static" sx={{ zIndex: 96 }}>
        <AppFooter>

        </AppFooter>
      </AppBar>
    </ThemeProvider>
  );
}
