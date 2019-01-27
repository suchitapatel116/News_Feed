package com.example.user.newsgateway;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


//A placeholder fragment containing a simple view
public class ArticlePlaceHolderFragment extends Fragment {

    private static final String TAG = "ArticlePlaceHolderFragm";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TOTAL_PAGE_NUMBER = "total_pages";
    private static final String ARG_ARTICLE_DATA = "article_data";
    ImageView ivImg;
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm");
    private String url = "";

    //Returns a new instance of this fragment for the given section number
    public static ArticlePlaceHolderFragment newInstance(int sectionNumber, ArticlesData data, int totalPages)
    {
        ArticlePlaceHolderFragment fragment = new ArticlePlaceHolderFragment();
        Bundle args = new Bundle(1);
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putInt(ARG_TOTAL_PAGE_NUMBER, totalPages);
        args.putSerializable(ARG_ARTICLE_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_article_place_holder, container, false);

        TextView tvTitle = rootView.findViewById(R.id.tv_title);
        TextView tvAuthor = rootView.findViewById(R.id.tv_author);
        TextView tvDate = rootView.findViewById(R.id.tv_publish_date);
        TextView tvDesc = rootView.findViewById(R.id.tv_description);
        TextView tvPage = rootView.findViewById(R.id.tv_page_no);
        ivImg = rootView.findViewById(R.id.iv_img);

        ArticlesData dataObj = new ArticlesData();
        dataObj = (ArticlesData) getArguments().getSerializable(ARG_ARTICLE_DATA);

        if(dataObj.getArticle_title() != null && !dataObj.getArticle_title().equals("null")) {
            tvTitle.setText(dataObj.getArticle_title());

            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openWebsite();
                }
            });
        }
        if(dataObj.getArticle_author() != null && !dataObj.getArticle_author().equals("null")) {
            tvAuthor.setText(dataObj.getArticle_author());

            tvAuthor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openWebsite();
                }
            });
        }
        String date = dataObj.getArticle_publishedAt();
        if(date != null && !date.equals("") && !date.equals("null")) {
            //Date dt = null;
            try {
                date = date.replace("T", " ");
                date = date.replace("Z", "");
                date = sdf.format(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvDate.setText(date.toString());

            tvDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openWebsite();
                }
            });
        }
        if(dataObj.getArticle_description() != null && !dataObj.getArticle_description().equals("null")) {
            tvDesc.setText(dataObj.getArticle_description());

            tvDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openWebsite();
                }
            });
        }
        int total = getArguments().getInt(ARG_TOTAL_PAGE_NUMBER);
        int pg_no = getArguments().getInt(ARG_SECTION_NUMBER) + 1;
        tvPage.setText(getString(R.string.section_page_format, pg_no) + " "+ total);

        url = dataObj.getArticle_webUrl();
        addImage(dataObj);

        return rootView;
    }

    private void addImage(final ArticlesData dataObj)
    {
        if(dataObj != null)
        {
            if(dataObj.getArticle_urlToImage() != null && !dataObj.getArticle_urlToImage().equals(""))
            {
                Picasso picasso = new Picasso.Builder(getActivity().getApplicationContext()).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        // Here we try https if the http image attempt failed
                        final String url = dataObj.getArticle_urlToImage().replace("http:", "https:");
                        picasso.load(url)
                                .error(R.drawable.brokenimage)
                                .placeholder(R.drawable.placeholder)
                                .into(ivImg);
                    }
                }).build();
                picasso.load(dataObj.getArticle_urlToImage())
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(ivImg);

                ivImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openWebsite();
                    }
                });
            }
            else
            {
                Log.d(TAG, "addImage: Image not available");
            }
        }
    }

    private void openWebsite() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}








/**
 * package com.example.user.newsgateway;

 import android.content.Context;
 import android.net.Uri;
 import android.os.Bundle;
 import android.app.Fragment;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;


 /**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArticlePlaceHolderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArticlePlaceHolderFragment#newInstance} factory method to
 * create an instance of this fragment.
 //////
public class ArticlePlaceHolderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ArticlePlaceHolderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArticlePlaceHolderFragment.
     /////
    // TODO: Rename and change types and number of parameters
    public static ArticlePlaceHolderFragment newInstance(String param1, String param2) {
        ArticlePlaceHolderFragment fragment = new ArticlePlaceHolderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article_place_holder, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     /////
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

 */
