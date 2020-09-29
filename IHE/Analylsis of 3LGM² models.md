# Analylsis of 3LGM² models #
The 3LGM² Tool offers a analysis function, which allows the user to answer given questions regarding selected elements.
Depending on the element type there are different questions offered by the analysis option:

***Application component:***

* On which physical data processing components is it installed?
* Which enterprise functions does this physical data processing component support?
* Which object types are stored?
* Which object types does this component send or receive?
* Which communication links does this component have?	

***Communication standard:***

* Where is this communication standard applied?

***Entity type:***

* Where is this object type communicated?
* Which interfaces can communicate this object type?
* Where is this object type stored?
* Which components belong to the data domain of this object type?

***ETDT-Combination:***

* Where is this ETDT-Combination transmitted, and which object types are communicated?

***ETMT-Combination:***

* Where is this ETNTCombination transmitted, and which object types are communicated?

***Event type:***

* Where are message types and document types connected to this event type transmitted, and which object types are communicated?

***Function:***

* Which application components and which physical data processing components are needed to achieve this enterprise function?
* Which application components are needed to achieve this enterprise function?

***Location:***

* Which physical data processing components are located at this location?

***Message type, Document type:***

* Where is this message or document type transmitted, and which object types are communicated?

***Organizational unit:***

* Which components belong to the scenario of this organizational unit?

***Physical data processing component:***

* Which database systems are installed on this physical data processing component, and which object types are stored in them?
* Which computer-based application components are installed on this physical data processing component?
* Which computer-based application components are installed on this physical data processing component, and which enterprise functions do they support?

***Software product:***

* On which application components it is installed?


## Accessing the analysis function ##
There are two main ways to access these functions.
The first option would be the **Analysis** option on the top menu bar.

![analysisMenuBar.PNG](https://bitbucket.org/repo/9L6rMz/images/817250633-analysisMenuBar.PNG)

### 1. Repository ###
This opens following window:

![analysisRepository.PNG](https://bitbucket.org/repo/9L6rMz/images/3171069154-analysisRepository.PNG)

Here you can select the wanted analysis. If you have selected an element beforehand, the analysis will be done for the selected element. Otherwise the Analysis will be done for every component.

### 2. Right Click ###
It is also possible to just right click on the component you want to analyze. This will offer you the analysis options available for this element type.

![analysisRightClickOption.PNG](https://bitbucket.org/repo/9L6rMz/images/2233704293-analysisRightClickOption.PNG)

### Results ###
Basically the analysis will highlight all relevant components, based on the question chosen.
For an example we will check on **which application components and physical data processing components are needed to achieve the enterprise function *"Request for findings Imaging diagnostics / radiotherapy / nuclear medicine"* **

![analysisResults1.PNG](https://bitbucket.org/repo/9L6rMz/images/748800925-analysisResults1.PNG)

As you can see, the necessary application component and the physical data processing components are highlighted. The connections will also be highlighted. To have these shown for inter-layer connections you can either right click on an empty space on a layer or right click the component for which you want the connections to be shown and choose **Show Configurations**.

![analysisResultsShowConf.PNG](https://bitbucket.org/repo/9L6rMz/images/551341026-analysisResultsShowConf.PNG)

![analysisResults2.PNG](https://bitbucket.org/repo/9L6rMz/images/550805756-analysisResults2.PNG)

**To reset the results** you can click the **Reset Results** option by going to **Analysis** in the menu bar.

![analysisResetResults.PNG](https://bitbucket.org/repo/9L6rMz/images/3840971573-analysisResetResults.PNG)

### Editor ###
You can define your own analysis options. To do this, choose the **Editor** in the analysis tab.

![analysisTabEditor.PNG](https://bitbucket.org/repo/9L6rMz/images/2598217839-analysisTabEditor.PNG)

This will open following window:

![analysisEditorClean.PNG](https://bitbucket.org/repo/9L6rMz/images/2311358478-analysisEditorClean.PNG)

Here you can define your own rules.

On the Left you have the choice of given element types. Then you can decide if it should or shouldn't be connected to the element you choose on the right. If nothing is chosen in the right side, then every option on the right is considered. With the plus button you can create further conditions following the choice of the element type from above.

Afterwards you have the option to analyze an object of your choice with the ***Start analysis*** button. Or save it into the repository.

If you chose to save it, then you can call it either through option **1. Repository** or **2. Right Click**.

#### Example ####
Let's say the analysis **"Which application components are needed to achieve this enterprise function?"** didn't exist and we would want to create this.

Since this analysis starts from a function, first we choose the **Function** element type. This object should be connected to a **Function organization unit combination**. This then is connected to the **Application component configuration** which then leads us to the final option the **Computer-based appl. component**.

![analysisEditor.PNG](https://bitbucket.org/repo/9L6rMz/images/1671067454-analysisEditor.PNG)

### Redundancy Analysis ###
Furthermore you have the options to do certain redundancy analysis.

![analysisTabRedundancy.PNG](https://bitbucket.org/repo/9L6rMz/images/2206756302-analysisTabRedundancy.PNG)

This opens a window, allowing you to choose between following options:

* Application components concerning Functions

* Computer-based appl. components concerning Functions

* Paper-based appl. components concerning Functions

* Software products concerning Functions

* Database systems concerning Entity types

* Self defined analysis

#### Redundancy of Functions regarding Application component configurations ####

This option shows a number on the bottom right corner of each **function**.

![redundancyAnalysisFunction.PNG](https://bitbucket.org/repo/9L6rMz/images/2893024423-redundancyAnalysisFunction.PNG)

* **1**: this function is supported by multiple application components

* **0**: this function is supported by a single application component

* **-1**: this function is not supported by any application component

On the top you can see the:

* **Redundancy factor**: percentage of functions, that are supported by multiple applications

* **Undersaturation factor**: percentage of functions, that aren't supported by any application

#### Redundancy of Entity types regarding Logical memories ####

This option shows a number on the bottom right corner of each **entity type**.

![redundancyAnalysisEntity.PNG](https://bitbucket.org/repo/9L6rMz/images/3867167014-redundancyAnalysisEntity.PNG)

* **1**: this entity type is stored in multiple database systems

* **0**: this entity type is stored in a single database system

* **-1**: this entity type is not stored in any database system

On the top you can see the:

* **Redundancy factor**: percentage of entity types, that are stored in database systems

* **Undersaturation factor**: percentage of entity types, that aren't stored in any database system