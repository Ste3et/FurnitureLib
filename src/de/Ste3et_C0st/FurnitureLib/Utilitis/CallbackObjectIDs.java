package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.HashSet;

import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public abstract interface CallbackObjectIDs
{
  public abstract void onResult(HashSet<ObjectID> idList);
}
