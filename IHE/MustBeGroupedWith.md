## Must be grouped with ##

IHE requires certain actors to be grouped with specific other actors.
This means, that when inserting actors in application systems, there might be a need to add specific actors.

The sources for these groupings are the technical frameworks released on the [IHE website](https://www.ihe.net/resources/technical_frameworks/#top).

3LGM supports this by having these attributes stored in each actor. The tool will display error messages, when actors aren't grouped with the necessary actors.

The tool offers three options to resolve this error:
1. Option: Property dialog for error-correcting
2. Option: Remove inconsistent Element
3. Option: Connect


## Example ##

In the following image we modeled a Patient Management System.

Starting from the bottom, first we will create the **application system** for patient management.
For the Patient Management System we will add the **Patient Identity Source** from the **Patient Identifier Cross-referencing profile (PIX)**

![patientManagementSystem.PNG](https://bitbucket.org/repo/9L6rMz/images/3102863631-patientManagementSystem.PNG)

The **technical framework** states following groupings for the actors in the **Patient Identity Source** (PIX) profile.

![mustbegroupedwith.PNG](https://bitbucket.org/repo/9L6rMz/images/3600768256-mustbegroupedwith.PNG)

So when we **add** the **Patient Identity Source** from the Template Browser

![addingPIXSource.PNG](https://bitbucket.org/repo/9L6rMz/images/4292716758-addingPIXSource.PNG)

we get following **error message** on the **bottom of the 3LGM tool** (Consistency Check view).

![groupingError.PNG](https://bitbucket.org/repo/9L6rMz/images/2745019955-groupingError.PNG)

The error message states, that the actor **needs to be grouped with another actor**. By **right clicking on the error** you have three options to resolv this issue.

### 1. Option: Property dialog for error-correcting
![firstOption.PNG](https://bitbucket.org/repo/9L6rMz/images/1190603611-firstOption.PNG)

The first option will open the property dialogue window showing the vacant IHE Actors on the right.
![pDialogue.PNG](https://bitbucket.org/repo/9L6rMz/images/2123300710-pDialogue.PNG)

Doubleclick on the added Actor (Patient Identity Source) and it will open another window. Here go to the tab **Grouping** to find out which actors have to be grouped with the current one. 

![pDialogue2.PNG](https://bitbucket.org/repo/9L6rMz/images/3957901489-pDialogue2.PNG)

Now go back to the first window and select the vacant Actor(s), in this case the Time Client and add it to your application system by clicking on the **<** button.

![pDialogue3.PNG](https://bitbucket.org/repo/9L6rMz/images/2229559516-pDialogue3.PNG)

### 2. Option: Remove inconsistent Element
![secondOption.PNG](https://bitbucket.org/repo/9L6rMz/images/4130889738-secondOption.PNG)

This option will just remove the element, which generated the inconsistency error.

### 3. Option: Connect

![groupingErrorResolve.PNG](https://bitbucket.org/repo/9L6rMz/images/3594253812-groupingErrorResolve.PNG)

This will add the missing actor to the application system. There might be a need for readjusting the position but the error message will be resolved by this.