package eWAGI_Firms;
import java.util.Comparator;
public class VacancyComparator implements Comparator<Vacancy>{
	// override the compare() method 
    public int compare(Vacancy s1, Vacancy s2) 
    { 
        if (s1.wage == s2.wage) 
            return 0; 
        else if (s1.wage > s2.wage) 
            return -1; 
        else
            return 1; 
    } 
}