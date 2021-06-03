package com.example.flex.Tasks

import java.io.Serializable
import java.util.*


class Task: Serializable {
    //date
    //week
    //status (stage)
    //type (D,P)
    // stage note
    // calendar icon (activity related)

    //type direct
    //name
    //address
    //deadline
    //add priority -> change color if high priority oranger ki tebda mazel se3a / 7amra si t3adet(done)
    //timestamp lel notes
    //report phase ((done --> idea check))
    //show medicines (done)
    //change button name from update to save (Done)
    //tangiz lel meeting le (done)
    // si pharmacy waiting room fi blasetha queue (Done)
    // ycochi el hajet li bech yreportihom + ynajem yzid
    // add table lel dweyet lkol lmawjoud fil base (done)
    // add order lel pharmacy((Done --> idea check))
    //once save clicked --> cant open again the task(Done)
    //0 7amra  / orange a9al men 3 / les alerts in dashboard
    //overwrite el onBackPressed to fix the BUG (Done)
    //google Maps Feature included (Done --> to refine later)
    //notification system al tasks (Done --> to check how it works)
    //button ytala3 el liste + button sghir yupdati(Done)
    //change order dialog b fragments(Done)
    //ad esm el doctor lel card view + task fragment(Done)
    // alert dialog to verify if he wants to update med number or not (Done)
    //dashboard work(Done--> to check)
    //change the order name in the DB (Done)
    // button order blastou louta (Done)
    //list todhher kan fil meeting(Done)
    //frag jdid (done)
    //fix button styles
    // change notif message you have n tasks to do in the next 2 days + you have n overdue tasks(Done)
    //notif benhar
    //zid button details fil dash
    // change update -> save
    //nahi exit 2button bark
    //add checkboxes fil report
    // textbox lel pourcentage
    var r_u_id: String? = null
    var date: String? = null //key
    var description: String? = null
    var week: String? = null
    var stage: String? = null
    var type: String? = null
    var report:String = ""
    var stage_notes = mutableMapOf(
        "On The Way" to "",
        "Waiting Room" to "",
        "Meeting" to ""
    )
    var address: String? = null
    var Destination_Name : String? = null
    var done: Boolean = false
    var deadline: Date? = null
    var closed: Boolean = false


    constructor() {}
    constructor(date: String?, description: String?, dest: String?,address: String? ,type:String?,deadline:Date?) {//week: String?, status: String?,address: String? ,type: String?) {
        this.date = date
        this.deadline = deadline
        this.description = description
        this.Destination_Name = dest
        this.address = address
        this.type = type
        this.report = ""
        this.closed = false
    }

    fun getTaskDone(): Boolean {
        return done
    }
    fun getTaskReport(): String? {
        return report
    }
    fun getTaskuid(): String? {
        return r_u_id
    }
    fun getTaskDescription(): String? {
        return description
    }
    fun getTaskDate(): String? {
        return date
    }

    fun getTaskStage(): String? {
        return stage
    }
    fun getTaskDestName(): String? {
        return Destination_Name
    }
    fun getTaskAddress(): String? {
        return address
    }

    fun getTaskType(): String? {
        return type
    }
    fun getTaskStageNote(stage: String): String? {
        return stage_notes[stage]
    }

    fun setTaskStageNote(stage: String,value:String) {
        stage_notes[stage] = value
    }

    fun getTaskDeadline(): Date? {
        return deadline
    }

    fun getClosedTask(): Boolean {
        return closed
    }



}