package com.frenchfriedtechnology.freelancer;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by matteo on 30/06/16.
 */
public class FreelancerJob implements JobCreator {
    @Override
    public Job create(String tag) {
        switch (tag){
            case ScheduledNotification.TAG:
                return  new ScheduledNotification();
        }
        return null;
    }
}
