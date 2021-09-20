package de.imise.tool3lgm;

import static de.imise.tool3lgm.userproperties.UserProperties.USER_HOME_3LGM_DIR;
import static de.imise.tool3lgm.userproperties.UserProperties.USER_HOME_DIR_NAME;
import static de.imise.tool3lgm.userproperties.UserProperties.USER_INFO_FILE;

import java.io.File;

/**
 * Class to remove/rearrange/change old things into new things.
 *
 * @author AXS (21.06.2021)
 */
public class DeprecatedHandler {

    /**
     * As the tool starts it should check the old location of the userproperties
     * file and if it exists cut and paste it to the new .3lgm folder
     */
    public static void relocateUserInfo() {
        //the currently valid 3lgm-userHome dir exists -> return
        if (USER_HOME_3LGM_DIR.isDirectory()) {
            return;
        }
        //In version 4.4.1_dev we moved all files to a ".3lgm" folder in the
        //UserHome and then (also still in version 4.4.1_dev) we moved it to the
        //folder "3lgm" (without dot). If the folder with dot exists, it is
        //renamed to the one without dot.
        File deprecated_3lgmHomeDirV_4_1_1_dev = new File(USER_HOME_DIR_NAME, ".3lgm");
        if (deprecated_3lgmHomeDirV_4_1_1_dev.exists()) {
            deprecated_3lgmHomeDirV_4_1_1_dev.renameTo(USER_HOME_3LGM_DIR);
            //rename the ".tool3lgm2UserInfo" into "tool3lgm2UserInfo.config"
            File deprecated_UserInfoFile = new File(USER_HOME_3LGM_DIR, ".tool3lgm2UserInfo");
            if (deprecated_UserInfoFile.exists()) {
                deprecated_UserInfoFile.renameTo(USER_INFO_FILE);
            }
            return;
        }

        //Before version 4.4.1_dev the 3lgm files were located directly in the
        //userHome dir, so we here move only the old .tool3lgmUserInfo into the
        //new directory.
        if (!USER_HOME_3LGM_DIR.isDirectory()) {
            USER_HOME_3LGM_DIR.mkdirs();
        }
        File deprecated_UserInfoFile = new File(USER_HOME_DIR_NAME, ".tool3lgm2UserInfo");
        if (deprecated_UserInfoFile.exists()) {
            deprecated_UserInfoFile.renameTo(USER_INFO_FILE);
        }
        renameAnalysisFile("en");
        renameAnalysisFile("de");
    }

    /**
     * Copies the analysis files
     *
     * @param languageCode "en" or "de"
     */
    private static void renameAnalysisFile(final String languageCode) {
        String anlyseFileName = "Tool3lgm_" + languageCode + ".analysis";//Tool3lgmConstants.getUserHomeAnlyseFileNameForLanguage(languageCode);
        File deprecated_AnalysisFile = new File(USER_HOME_DIR_NAME, anlyseFileName);
        if (deprecated_AnalysisFile.exists()) {
            File new_AnalysisFile = new File(USER_HOME_3LGM_DIR, anlyseFileName);
            deprecated_AnalysisFile.renameTo(new_AnalysisFile);
        }
    }

}
